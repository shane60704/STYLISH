package org.example.stylish.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.RedisException;
import org.example.stylish.dao.impl.AdminDaoImpl;
import org.example.stylish.dto.CampaignRequest;
import org.example.stylish.model.Campaign;
import org.example.stylish.service.AdminService;
import org.example.stylish.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.QueryTimeoutException;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.base-url}")
    private String baseUrl;

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private static final String CACHE_KEY = "campaigns";

    private final AdminDaoImpl adminDao;
    private final ObjectMapper objectMapper;
    private final CacheUtil cacheUtil;

    @Autowired
    public AdminServiceImpl(AdminDaoImpl adminDao, ObjectMapper objectMapper, CacheUtil cacheUtil) {
        this.adminDao = adminDao;
        this.objectMapper = objectMapper;
        this.cacheUtil = cacheUtil;
    }

    @Override
    public ResponseEntity<?> addCampaign(CampaignRequest campaignRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String campaignPicturePath = saveFile(campaignRequest.getPicture());
            // Determine whether the campaign for the given product exists.
            if (adminDao.getCampaignByProductId(campaignRequest.getProductId()) == null) {
                int rowsAffected = adminDao.createCampaign(campaignRequest, campaignPicturePath);
                if (rowsAffected > 0) {
                    // After successfully adding the campaign, clear the cache.
                    cacheUtil.deleteFromCache(CACHE_KEY);
                    log.info("Successfully added the campaign and cleared the cache.");
                    response.put("message", "Campaign created successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "Failed to create campaign.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                int rowsAffected = adminDao.updateCampaign(campaignRequest, campaignPicturePath);
                if (rowsAffected > 0) {
                    // After successfully updating the campaign, clear the cache.
                    cacheUtil.deleteFromCache(CACHE_KEY);
                    log.info("Successfully updated the campaign and cleared the cache.");
                    response.put("message", "Campaign updated successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "Failed to update campaign.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error(e.getMessage());
            response.put("message", "Campaign added or updated successfully.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Error saving file");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            response.put("message", "Campaign updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getAllCampaigns() {

        Map<String, Object> response = new HashMap<>();

        //Check if there is a corresponding key in the cache
        try {
            if (cacheUtil.hasKey(CACHE_KEY)) {
                String cachedData = cacheUtil.getFromCache(CACHE_KEY);
                log.info("Cache hit.");
                List<Campaign> campaigns = objectMapper.readValue(cachedData, new TypeReference<List<Campaign>>() {
                });
                response.put("data", campaigns);
                return ResponseEntity.ok(response);
            }
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException | QueryTimeoutException e) {
            log.error("Error accessing Redis, falling back to database.", e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        //If Redis encounters an error or if the requested key is not found in the cache, retrieve the data from the database.
        try {
            log.info("Cache miss.");
            List<Campaign> campaigns = getCampaignsFromDatabase();
            log.info("Returning campaigns data from database.");
            String campaignsJson = objectMapper.writeValueAsString(campaigns);
            cacheUtil.saveToCache(CACHE_KEY, campaignsJson);
            log.info("saving data to cache.");
        } catch (RuntimeException e) {
            log.error("Error accessing Redis, falling back to database.", e);
        } catch (IOException e) {
            log.error(e.getMessage());
            response.put("data", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        List<Campaign> campaigns = getCampaignsFromDatabase();
        response.put("data", campaigns);
        return ResponseEntity.ok(response);
    }

    private List<Campaign> getCampaignsFromDatabase() {
        return adminDao.getAllCampaigns();
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());
        return baseUrl + "/" + fileName;
    }

}

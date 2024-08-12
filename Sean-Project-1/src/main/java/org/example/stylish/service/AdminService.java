package org.example.stylish.service;

import org.example.stylish.dto.CampaignRequest;
import org.springframework.http.ResponseEntity;


public interface AdminService {
    ResponseEntity<?> addCampaign(CampaignRequest campaignRequest);

    ResponseEntity<?> getAllCampaigns();
}

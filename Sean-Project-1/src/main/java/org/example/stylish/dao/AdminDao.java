package org.example.stylish.dao;

import org.example.stylish.dto.CampaignRequest;
import org.example.stylish.model.Campaign;

import java.util.List;

public interface AdminDao {
    int createCampaign(CampaignRequest campaignRequest, String campaignPicturePath);

    int updateCampaign(CampaignRequest campaignRequest, String campaignPicturePath);

    Campaign getCampaignByProductId(int productId);

    List<Campaign> getAllCampaigns();


}

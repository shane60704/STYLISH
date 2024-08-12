package org.example.stylish.controller;

import org.example.stylish.dto.CampaignRequest;
import org.example.stylish.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1.0/marketing/")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(final AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/addCampaign")
    public ResponseEntity<?> addCampaign(CampaignRequest campaignRequest) {
        return adminService.addCampaign(campaignRequest);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<?> getCampaigns() {
        return adminService.getAllCampaigns();
    }

}

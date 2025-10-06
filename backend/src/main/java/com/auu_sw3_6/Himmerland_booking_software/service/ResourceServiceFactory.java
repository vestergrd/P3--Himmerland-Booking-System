package com.auu_sw3_6.Himmerland_booking_software.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;

@Service
public class ResourceServiceFactory {
    
    @Autowired
    private HospitalityService hospitalityService;

    @Autowired
    private ToolService toolService;

    @Autowired
    private UtilityService utilityService;

public ResourceService<?> getServiceByType(ResourceType resourceType) {
    switch (resourceType) {
        case HOSPITALITY -> {
            return hospitalityService;
        }
        case TOOL -> {
            return toolService;
        }
        case UTILITY -> {
            return utilityService;
        }
        default -> throw new IllegalArgumentException("Unknown resource type: " + resourceType);
    }
}
}
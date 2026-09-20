package com.parksmart.controller;

import com.parksmart.dto.AnalyticsSummaryDTO;
import com.parksmart.dto.ApiResponse;
import com.parksmart.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ApiResponse<AnalyticsSummaryDTO> getAnalyticsSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AnalyticsSummaryDTO summary = analyticsService.getSummary(date != null ? date : LocalDate.now());
        return ApiResponse.success("Analytics summary retrieved successfully", summary);
    }
}

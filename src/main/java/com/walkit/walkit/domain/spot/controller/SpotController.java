package com.walkit.walkit.domain.spot.controller;

import com.walkit.walkit.common.dto.ApiResponse;
import com.walkit.walkit.domain.spot.dto.response.NearbySpotResponseDto;
import com.walkit.walkit.domain.spot.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Spot", description = "주변 스팟 조회 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/spots")
public class SpotController {

    private final SpotService spotService;

    @Operation(summary = "주변 스팟 검색", description = "경도(x), 위도(y), 반경, 검색어를 기반으로 주변 스팟 목록을 반환합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbySpotResponseDto>> getNearbySpots(
            @RequestParam String query,
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam(defaultValue = "1000") int radius,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "distance") String sort
    ) {
        List<NearbySpotResponseDto> spots = spotService.getNearbySpots(query, x, y, radius, size, sort);
        return ResponseEntity.ok(spots);
    }
}

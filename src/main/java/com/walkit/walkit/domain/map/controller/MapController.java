package com.walkit.walkit.domain.map.controller;

import com.walkit.walkit.domain.map.dto.response.FollowerLatestWalkResponseDto;
import com.walkit.walkit.domain.map.dto.response.FollowerRecentActivityResponseDto;
import com.walkit.walkit.domain.map.dto.response.FollowerWalkingRecordResponseDto;
import com.walkit.walkit.domain.map.service.MapService;
import com.walkit.walkit.global.security.jwt.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Map", description = "지도 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/maps")
public class MapController {

    private final MapService mapService;

    @Operation(summary = "팔로우 산책 기록 위치 조회", description = "지도 탭에서 나의 팔로우들의 최근 산책 기록 위치를 반경 내에서 조회합니다.")
    @GetMapping("/follower/walking-records")
    public ResponseEntity<List<FollowerWalkingRecordResponseDto>> getFollowerWalkingRecords(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1000") int radius
    ) {
        Long userId = userPrincipal.getUserId();
        List<FollowerWalkingRecordResponseDto> response = mapService.getFollowerWalkingRecords(userId, lat, lon, radius);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "팔로우 최근 활동 목록 조회", description = "나의 팔로우 목록을 가장 최근에 산책한 순서로 조회합니다.")
    @GetMapping("/follower/recent-activities")
    public ResponseEntity<List<FollowerRecentActivityResponseDto>> getFollowerRecentActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getUserId();
        List<FollowerRecentActivityResponseDto> response = mapService.getFollowerRecentActivities(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "팔로우 최근 산책 기록 상세 조회", description = "특정 팔로우의 가장 최근 산책 기록을 상세 조회합니다.")
    @GetMapping("/follower/{userId}/walking-records/latest")
    public ResponseEntity<FollowerLatestWalkResponseDto> getFollowerLatestWalk(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long userId
    ) {
        Long requesterId = userPrincipal.getUserId();
        FollowerLatestWalkResponseDto response = mapService.getFollowerLatestWalk(requesterId, userId);
        return ResponseEntity.ok(response);
    }
}

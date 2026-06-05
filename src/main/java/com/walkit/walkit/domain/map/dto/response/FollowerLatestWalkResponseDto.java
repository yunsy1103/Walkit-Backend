package com.walkit.walkit.domain.map.dto.response;

import com.walkit.walkit.domain.character.entity.Character;
import com.walkit.walkit.domain.character.enums.Grade;
import com.walkit.walkit.domain.user.entity.User;
import com.walkit.walkit.domain.walk.entity.Walk;
import com.walkit.walkit.domain.walk.dto.response.WalkPointResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class FollowerLatestWalkResponseDto {

    private int level;
    private Grade grade;
    private String nickName;
    private ResponseWalkRecordDto responseWalkRecordDto;
    private int likeCount;
    private boolean liked;

    @Getter
    @Builder
    public static class ResponseWalkRecordDto {
        private LocalDateTime createdDate;
        private String imageUrl;
        private List<WalkPointResponseDto> points;
        private Long totalTime;
        private Integer stepCount;
    }

    public static FollowerLatestWalkResponseDto of(User followUser, Walk walk, int likeCount, boolean liked) {
        Character character = followUser.getCharacter();

        List<WalkPointResponseDto> points = walk.getPoints().stream()
                .map(p -> new WalkPointResponseDto(p.getLatitude(), p.getLongitude(), p.getRecordedAt()))
                .toList();

        return FollowerLatestWalkResponseDto.builder()
                .level(character.getLevel())
                .grade(character.getGrade())
                .nickName(followUser.getNickname())
                .responseWalkRecordDto(ResponseWalkRecordDto.builder()
                        .createdDate(walk.getCreatedDate())
                        .imageUrl(walk.getImageUrl())
                        .points(points)
                        .totalTime(walk.getTotalTime())
                        .stepCount(walk.getStepCount())
                        .build())
                .likeCount(likeCount)
                .liked(liked)
                .build();
    }
}

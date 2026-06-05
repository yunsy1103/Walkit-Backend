package com.walkit.walkit.domain.spot.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter

@Builder
public class NearbySpotResponseDto {

    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String distance;
    private String placeUrl;
    private int blogReviewCount;
    private String blogReviewLink;
    private String thumbnailUrl;
    private String x;
    private String y;
}

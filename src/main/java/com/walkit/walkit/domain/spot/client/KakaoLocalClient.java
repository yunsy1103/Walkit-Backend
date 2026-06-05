package com.walkit.walkit.domain.spot.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Component
public class KakaoLocalClient {

    private final WebClient webClient;

    public KakaoLocalClient(@Value("${kakao.rest-api-key}") String restApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + restApiKey)
                .build();
    }

    public List<KakaoPlace> searchKeyword(String query, double x, double y, int radius, int size, String sort) {
        try {
            KakaoLocalResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("x", x)
                            .queryParam("y", y)
                            .queryParam("radius", radius)
                            .queryParam("size", size)
                            .queryParam("sort", sort)
                            .build())
                    .retrieve()
                    .bodyToMono(KakaoLocalResponse.class)
                    .block();

            return response != null ? response.getDocuments() : List.of();
        } catch (WebClientResponseException e) {
            log.error("[KakaoLocalClient] 호출 실패 - status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoLocalResponse {
        private List<KakaoPlace> documents;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoPlace {
        @JsonProperty("place_name")
        private String placeName;

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("road_address_name")
        private String roadAddressName;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("place_url")
        private String placeUrl;

        @JsonProperty("x")
        private String x;

        @JsonProperty("y")
        private String y;
    }
}

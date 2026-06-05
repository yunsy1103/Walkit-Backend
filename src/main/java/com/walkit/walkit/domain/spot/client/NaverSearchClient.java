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
public class NaverSearchClient {

    private final WebClient webClient;

    public NaverSearchClient(
            @Value("${naver.search.client-id}") String clientId,
            @Value("${naver.search.client-secret}") String clientSecret
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public NaverBlogResult searchBlog(String query) {
        try {
            NaverBlogResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search/blog.json")
                            .queryParam("query", query)
                            .queryParam("display", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(NaverBlogResponse.class)
                    .block();

            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                return new NaverBlogResult(0, null);
            }
            return new NaverBlogResult(response.getTotal(), response.getItems().get(0).getLink());
        } catch (WebClientResponseException e) {
            log.warn("[NaverSearchClient] 블로그 검색 실패 - status={}, query={}", e.getStatusCode(), query);
            return new NaverBlogResult(0, null);
        }
    }

    public String searchImage(String query) {
        try {
            NaverImageResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search/image")
                            .queryParam("query", query)
                            .queryParam("display", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(NaverImageResponse.class)
                    .block();

            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                return null;
            }
            return response.getItems().get(0).getLink();
        } catch (WebClientResponseException e) {
            log.warn("[NaverSearchClient] 이미지 검색 실패 - status={}, query={}", e.getStatusCode(), query);
            return null;
        }
    }

    // --- Blog ---
    @Getter
    @NoArgsConstructor
    public static class NaverBlogResponse {
        private int total;
        private List<BlogItem> items;
    }

    @Getter
    @NoArgsConstructor
    public static class BlogItem {
        private String link;
    }

    public record NaverBlogResult(int total, String link) {}

    // --- Image ---
    @Getter
    @NoArgsConstructor
    public static class NaverImageResponse {
        private List<ImageItem> items;
    }

    @Getter
    @NoArgsConstructor
    public static class ImageItem {
        @JsonProperty("link")
        private String link;
    }
}

package com.resumeiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SuggestionResponse {
    private Long id;
    private String category;
    private String text;
    private Integer priority;
    private String section;
    private String relatedKeyword;
}

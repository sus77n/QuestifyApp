package com.example.iquiz.dto.answer;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MatchingPair {
    private String leftHeader;
    private String rightHeader;

}
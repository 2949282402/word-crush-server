package com.wordcrush.server.dto.request;

public record RankingRequest(
        Integer gameType,
        Integer topN
) {
}

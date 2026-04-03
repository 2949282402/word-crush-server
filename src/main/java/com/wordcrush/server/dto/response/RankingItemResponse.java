package com.wordcrush.server.dto.response;

public record RankingItemResponse(
        String username,
        Integer score,
        String time,
        Long avatarVersion
) {
}

package com.wordcrush.server.dto.request;

public record DeleteGameRecordRequest(
        String username,
        Integer gameType,
        Integer score,
        String time
) {
}

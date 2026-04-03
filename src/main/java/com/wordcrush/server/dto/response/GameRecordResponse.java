package com.wordcrush.server.dto.response;

import java.util.List;

public record GameRecordResponse(
        String username,
        Integer gameType,
        Integer score,
        String time,
        List<String> learnedWords
) {
}

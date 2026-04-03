package com.wordcrush.server.module.game.record.controller;

import com.wordcrush.server.common.api.LegacyApiResponse;
import com.wordcrush.server.common.exception.BusinessException;
import com.wordcrush.server.module.game.ranking.dto.RankingRequest;
import com.wordcrush.server.module.game.ranking.response.RankingItemResponse;
import com.wordcrush.server.module.game.ranking.service.RankingService;
import com.wordcrush.server.module.game.record.dto.DeleteGameRecordRequest;
import com.wordcrush.server.module.game.record.dto.SaveGameRecordRequest;
import com.wordcrush.server.module.game.record.dto.UsernameRequest;
import com.wordcrush.server.module.game.record.response.GameRecordResponse;
import com.wordcrush.server.module.game.record.service.GameRecordService;
import com.wordcrush.server.security.AuthenticatedUserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "GameRecord", description = "鎺掕姒滀笌娓告垙璁板綍鎺ュ彛")
public class GameRecordController {

    private final GameRecordService gameRecordService;
    private final RankingService rankingService;

    @PostMapping("/getTopNRecord")
    @Operation(summary = "鑾峰彇鎺掕姒?")
    public LegacyApiResponse<List<RankingItemResponse>> getTopNRecord(@RequestBody RankingRequest request) {
        return executeLegacy(() -> rankingService.getTopRankings(request));
    }

    @PostMapping("/addGameRecord")
    @Operation(summary = "鏂板娓告垙璁板綍")
    public LegacyApiResponse<String> addGameRecord(@RequestBody SaveGameRecordRequest request) {
        return executeLegacy(() -> {
            if (request != null) {
                AuthenticatedUserContext.requireAccessToUsername(request.username());
            }
            gameRecordService.addGameRecord(request);
            return "ok";
        });
    }

    @PostMapping("/deleteGameRecord")
    @Operation(summary = "鍒犻櫎娓告垙璁板綍")
    public LegacyApiResponse<String> deleteGameRecord(@RequestBody DeleteGameRecordRequest request) {
        return executeLegacy(() -> {
            if (request != null) {
                AuthenticatedUserContext.requireAccessToUsername(request.username());
            }
            gameRecordService.deleteGameRecord(request);
            return "deleted";
        });
    }

    @PostMapping("/getAllGameRecord")
    @Operation(summary = "鑾峰彇鐢ㄦ埛鍏ㄩ儴娓告垙璁板綍")
    public LegacyApiResponse<List<GameRecordResponse>> getAllGameRecord(@RequestBody UsernameRequest request) {
        return executeLegacy(() -> {
            if (request != null) {
                AuthenticatedUserContext.requireAccessToUsername(request.username());
            }
            return gameRecordService.getAllGameRecords(request);
        });
    }

    private <T> LegacyApiResponse<T> executeLegacy(Supplier<T> supplier) {
        try {
            return LegacyApiResponse.success(supplier.get());
        } catch (BusinessException exception) {
            return failResponse(exception.getMessage());
        } catch (Exception exception) {
            log.error("Legacy endpoint error", exception);
            return failResponse("internal server error");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> LegacyApiResponse<T> failResponse(String message) {
        return (LegacyApiResponse<T>) LegacyApiResponse.fail(message);
    }
}

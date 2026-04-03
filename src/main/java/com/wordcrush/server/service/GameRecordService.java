package com.wordcrush.server.service;

import com.wordcrush.server.common.enums.GameType;
import com.wordcrush.server.common.exception.BusinessException;
import com.wordcrush.server.domain.entity.GameRecord;
import com.wordcrush.server.domain.entity.GameRecordWord;
import com.wordcrush.server.domain.entity.UserAccount;
import com.wordcrush.server.domain.repository.GameRecordRepository;
import com.wordcrush.server.domain.repository.UserAccountRepository;
import com.wordcrush.server.dto.request.DeleteGameRecordRequest;
import com.wordcrush.server.dto.request.RankingRequest;
import com.wordcrush.server.dto.request.SaveGameRecordRequest;
import com.wordcrush.server.dto.request.UsernameRequest;
import com.wordcrush.server.dto.response.GameRecordResponse;
import com.wordcrush.server.dto.response.RankingItemResponse;
import com.wordcrush.server.support.TimeFormats;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GameRecordService {

    private final GameRecordRepository gameRecordRepository;
    private final UserAccountRepository userAccountRepository;
    private final AvatarStorageService avatarStorageService;

    @Transactional(readOnly = true)
    public List<RankingItemResponse> getTopRankings(RankingRequest request) {
        if (request == null) {
            throw new BusinessException("request body must not be null");
        }
        GameType.fromCode(request.gameType());
        if (request.topN() == null || request.topN() <= 0) {
            throw new BusinessException("topN must be greater than 0");
        }

        List<GameRecord> records = gameRecordRepository.findAllByGameTypeOrderByScoreDescPlayedAtAsc(request.gameType());
        LinkedHashMap<String, RankingItemResponse> rankingMap = new LinkedHashMap<>();
        for (GameRecord record : records) {
            String username = record.getUser().getUsername();
            if (rankingMap.containsKey(username)) {
                continue;
            }
            rankingMap.put(username, new RankingItemResponse(
                    username,
                    record.getScore(),
                    TimeFormats.formatGameTime(record.getPlayedAt()),
                    avatarStorageService.avatarVersion(username)
            ));
            if (rankingMap.size() >= request.topN()) {
                break;
            }
        }
        return new ArrayList<>(rankingMap.values());
    }

    @Transactional
    public void addGameRecord(SaveGameRecordRequest request) {
        if (request == null) {
            throw new BusinessException("request body must not be null");
        }
        validateUsername(request.username());
        GameType.fromCode(request.gameType());
        if (request.score() == null || request.score() < 0) {
            throw new BusinessException("score must be >= 0");
        }
        if (!StringUtils.hasText(request.time())) {
            throw new BusinessException("time must not be blank");
        }

        UserAccount user = loadUser(request.username());
        LocalDateTime playedAt = TimeFormats.parseGameTime(request.time());
        if (gameRecordRepository.existsByUserUsernameAndGameTypeAndScoreAndPlayedAt(
                request.username(),
                request.gameType(),
                request.score(),
                playedAt)) {
            return;
        }

        GameRecord record = new GameRecord();
        record.setUser(user);
        record.setGameType(request.gameType());
        record.setScore(request.score());
        record.setPlayedAt(playedAt);
        record.replaceLearnedWords(sanitizeLearnedWords(request.learnedWords()));
        gameRecordRepository.save(record);
    }

    @Transactional
    public void deleteGameRecord(DeleteGameRecordRequest request) {
        if (request == null) {
            throw new BusinessException("request body must not be null");
        }
        validateUsername(request.username());
        GameType.fromCode(request.gameType());
        if (request.score() == null || request.score() < 0) {
            throw new BusinessException("score must be >= 0");
        }
        if (!StringUtils.hasText(request.time())) {
            throw new BusinessException("time must not be blank");
        }

        LocalDateTime playedAt = TimeFormats.parseGameTime(request.time());
        GameRecord record = gameRecordRepository
                .findFirstByUserUsernameAndGameTypeAndScoreAndPlayedAt(
                        request.username(),
                        request.gameType(),
                        request.score(),
                        playedAt)
                .orElseThrow(() -> new BusinessException(404, "game record not found"));
        gameRecordRepository.delete(record);
    }

    @Transactional(readOnly = true)
    public List<GameRecordResponse> getAllGameRecords(UsernameRequest request) {
        if (request == null) {
            throw new BusinessException("request body must not be null");
        }
        validateUsername(request.username());
        loadUser(request.username());
        return gameRecordRepository.findByUserUsernameOrderByPlayedAtDesc(request.username())
                .stream()
                .map(record -> new GameRecordResponse(
                        record.getUser().getUsername(),
                        record.getGameType(),
                        record.getScore(),
                        TimeFormats.formatGameTime(record.getPlayedAt()),
                        record.getLearnedWords().stream().map(GameRecordWord::getWordContent).toList()
                ))
                .toList();
    }

    private UserAccount loadUser(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "user not found"));
    }

    private void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("username must not be blank");
        }
    }

    private List<String> sanitizeLearnedWords(List<String> learnedWords) {
        if (CollectionUtils.isEmpty(learnedWords)) {
            return List.of();
        }
        return learnedWords.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .toList();
    }
}

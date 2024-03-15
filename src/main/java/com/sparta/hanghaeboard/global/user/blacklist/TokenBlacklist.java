package com.sparta.hanghaeboard.global.user.blacklist;

import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class TokenBlacklist {

    private final Set<String> blacklist = new HashSet<>();

    // 토큰을 블랙리스트에 추가합니다.
    public synchronized void addToBlacklist(String token) {
        blacklist.add(token);
    }

    // 토큰이 블랙리스트에 있는지 확인합니다.
    public synchronized boolean isBlacklisted(String token) {
        return blacklist.contains(token);

    }
}

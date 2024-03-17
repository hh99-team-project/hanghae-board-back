package com.sparta.hanghaeboard.domain.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PagingDataResponseDto<D> {
    private final D responseDto;
    private final int nowPage;
    private final int startPage;
    private final int endPage;
    private final boolean hasNext;
    private final boolean hasPrev;
}

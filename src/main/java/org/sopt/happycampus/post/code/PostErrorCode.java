package org.sopt.happycampus.post.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

/**
 * 자료 도메인에서 발생하는 오류 코드 | code: 오류의 내응
 */
@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ResultCode {

    // 자료 상세 조회 실패
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "자료를 찾을 수 없습니다.");

    // 응답 Http 상태
    private final HttpStatus status;

    // 응답 메세지
    private final String message;
}

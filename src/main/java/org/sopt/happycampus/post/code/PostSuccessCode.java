package org.sopt.happycampus.post.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

/**
 * 자료 도메인의 성공 응답 코드
 */
@Getter
@RequiredArgsConstructor
public enum PostSuccessCode implements ResultCode {

    // 자료 상세 조회 성공
    POST_DETAIL_FOUND(HttpStatus.OK, "자료 상세 조회에 성공했습니다.");

    // 응답 HTTP 상태
    private final HttpStatus status;

    // 응답 메시지
    private final String message;
}

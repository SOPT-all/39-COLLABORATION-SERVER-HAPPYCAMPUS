package org.sopt.happycampus.user.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResultCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다.");

	private final HttpStatus status;
	private final String message;
}

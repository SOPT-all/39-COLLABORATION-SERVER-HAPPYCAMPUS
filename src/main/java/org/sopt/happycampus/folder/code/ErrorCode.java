package org.sopt.happycampus.folder.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResultCode {
	FOLDER_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 같은 이름의 폴더가 존재합니다.");

	private final HttpStatus status;
	private final String message;
}

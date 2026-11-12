package org.sopt.happycampus.folder.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResultCode {
	FOLDERS_FOUND(HttpStatus.OK, "폴더 목록을 조회했습니다."),
	FOLDER_CREATED(HttpStatus.CREATED, "폴더를 생성했습니다."),
	CART_ITEMS_ADDED_TO_FOLDER(HttpStatus.OK, "폴더에 자료를 추가했습니다.");

	private final HttpStatus status;
	private final String message;
}

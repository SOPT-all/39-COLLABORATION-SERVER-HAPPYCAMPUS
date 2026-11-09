package org.sopt.happycampus.cart.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode implements ResultCode {
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 자료가 존재하지 않습니다."),
	;

	private final HttpStatus Status;
	private final String message;

	private ErrorCode(HttpStatus Status, String message) {
		this.Status = Status;
		this.message = message;
	}
}

package org.sopt.happycampus.cart.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResultCode {
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 자료가 존재하지 않습니다."),

	/**
	 * 같은 사용자의 장바구니에 해당 자료가 이미 존재하는 경우
	 */
	CART_ITEM_DUPLICATED(HttpStatus.CONFLICT, "이미 장바구니에 담긴 자료입니다.");

	private final HttpStatus status;
	private final String message;
}


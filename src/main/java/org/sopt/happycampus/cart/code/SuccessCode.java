package org.sopt.happycampus.cart.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResultCode {
	CART_ITEM_DELETED(HttpStatus.OK, "장바구니에서 자료를 삭제했습니다."),

	/**
	 * 장바구니에 자료 추가 성공.
	 * 201
	 */
	CART_ITEM_ADDED(HttpStatus.CREATED,"장바구니에 자료를 담았습니다.");

	private final HttpStatus status;
	private final String message;
}

package org.sopt.happycampus.cart.code;

import org.sopt.happycampus.common.code.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum SuccessCode implements ResultCode {
	CART_ITEM_DELETED(HttpStatus.OK, "장바구니에서 자료를 삭제했습니다.");

	private final HttpStatus Status;
	private final String message;

	private SuccessCode(HttpStatus Status, String message) {
		this.Status = Status;
		this.message = message;
	}
}

package org.sopt.happycampus.cart.exception;

import org.sopt.happycampus.cart.code.ErrorCode;
import org.sopt.happycampus.common.exception.BaseException;

public class CartItemNotFoundException extends BaseException {
	public CartItemNotFoundException() {
		super(ErrorCode.CART_ITEM_NOT_FOUND);
	}
}

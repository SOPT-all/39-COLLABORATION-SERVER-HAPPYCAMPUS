package org.sopt.happycampus.cart.exception;

import org.sopt.happycampus.cart.code.ErrorCode;
import org.sopt.happycampus.common.exception.BaseException;

/**
 * 장바구니에 동일한 자료가 이미 존재할 때 발생하는 예외
 */
public class CartItemDuplicatedException extends BaseException {
    /**
     * 공통 예외 처리기에 전달할 중복 담기 오류 코드를 설정
     */
    public CartItemDuplicatedException() {
        super(ErrorCode.CART_ITEM_DUPLICATED);
    }
}

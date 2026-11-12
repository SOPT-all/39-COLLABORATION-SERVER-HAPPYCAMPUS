package org.sopt.happycampus.cart.dto.response;


import org.sopt.happycampus.cart.entity.CartItemEntity;

/**
 * 장바구니 담기 응답
 *
 * @param cartItemId 생성된 장바구니 항목 ID
 */
public record AddCartItemResponse (
        Long cartItemId
) {
    /**
     * 저장된 장바구니 항목을 응답 DTO로 변환
     *
     * @param cartItem 저장된 장바구니 항목
     * @return 생성된 항목 ID를 담은 응답
     */
    public static AddCartItemResponse from(CartItemEntity cartItem) {
        return new AddCartItemResponse(cartItem.getId());
    }
}

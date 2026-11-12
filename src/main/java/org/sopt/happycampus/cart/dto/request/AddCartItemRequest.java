package org.sopt.happycampus.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 장바구니 담기 요청
 *
 * @param postId 장바구니 담을 자료 ID
 */

public record AddCartItemRequest (
    @NotNull(message = "자료 ID를 입력해주세요.")
    @Positive(message = "자료 ID는 양수여야 합니다.")
    Long postId
) {

}

package org.sopt.happycampus.cart.controller;

import org.sopt.happycampus.common.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CartItem", description = "장바구니 API")
public interface CartItemControllerDocs {
	@Operation(summary = "장바구니 삭제", description = "장바구니에서 자료를 삭제합니다.")
	ResponseEntity<SuccessResponse<Void>> deleteCartItem(
		@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "cartItemId") Long cartItemId);
}

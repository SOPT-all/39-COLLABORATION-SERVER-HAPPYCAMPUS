package org.sopt.happycampus.cart.controller;

import java.util.List;

import org.sopt.happycampus.cart.dto.response.CartItemResponse;
import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.post.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Tag(name = "CartItem", description = "장바구니 API")
public interface CartItemControllerDocs {
	@Operation(summary = "장바구니 삭제", description = "장바구니에서 자료를 삭제합니다.")
	ResponseEntity<SuccessResponse<Void>> deleteCartItem(
		@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "cartItemId") Long cartItemId);

	@Operation(summary = "장바구니 자료 목록 조회", description = "장바구니 자료 목록을 조회합니다.")
	ResponseEntity<SuccessResponse<Page<CartItemResponse>>>  getCartItems(
		@PathVariable(name = "userId")  Long userId,
		@RequestParam(defaultValue = "0") @Min(0) Integer page,
		@RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size,
		@RequestParam(required = false) PostCategory category,
		@RequestParam(required = false) Long folderId
	);
}

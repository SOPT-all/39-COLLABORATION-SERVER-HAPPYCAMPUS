package org.sopt.happycampus.cart.controller;

import java.util.List;

import org.sopt.happycampus.cart.code.SuccessCode;
import org.sopt.happycampus.cart.dto.response.CartItemResponse;
import org.sopt.happycampus.cart.service.CartItemService;
import org.sopt.happycampus.common.response.ApiResponse;
import org.sopt.happycampus.common.response.SuccessResponse;
import org.sopt.happycampus.post.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path="/api/v1")
@RequiredArgsConstructor
public class CartItemController implements CartItemControllerDocs {
	private final CartItemService cartItemService;

	@DeleteMapping(path = "/users/{userId}/cart-items/{cartItemId}")
	public ResponseEntity<SuccessResponse<Void>>  deleteCartItem(
		@PathVariable(name = "userId") Long userId,
		@PathVariable(name = "cartItemId") Long cartItemId) {

		cartItemService.deleteCartItem(userId, cartItemId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.CART_ITEM_DELETED));
	}

	@GetMapping(path = "/users/{userId}/cart-items")
	public ResponseEntity<SuccessResponse<Page<CartItemResponse>>> getCartItems(
		@PathVariable(name = "userId")  Long userId,
		@RequestParam(defaultValue = "0") @Min(0) Integer page,
		@RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size,
		@RequestParam(required = false) PostCategory category,
		@RequestParam(required = false) Long folderId
	) {
		Page<CartItemResponse> cartItems = cartItemService.getCartItems(userId, page, size, category, folderId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.CART_ITEM_FOUND, cartItems));
	}

}

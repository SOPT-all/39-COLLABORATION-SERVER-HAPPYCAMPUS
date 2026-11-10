package org.sopt.happycampus.cart.controller;

import org.sopt.happycampus.cart.code.SuccessCode;
import org.sopt.happycampus.cart.service.CartItemService;
import org.sopt.happycampus.common.response.ApiResponse;
import org.sopt.happycampus.common.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

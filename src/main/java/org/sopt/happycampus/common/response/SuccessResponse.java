package org.sopt.happycampus.common.response;

import org.sopt.happycampus.common.code.ResultCode;

public record SuccessResponse<T>(
	int status,
	String code,
	String message,
	T data
) implements ApiResponse {
	public static <T> SuccessResponse<T> of(ResultCode resultCode, T data) {
		return new SuccessResponse<>(resultCode.getStatus().value(), resultCode.toString(), resultCode.getMessage(), data);
	}

	public static <T> SuccessResponse<T> of(ResultCode resultCode) {
		return new SuccessResponse<>(resultCode.getStatus().value(), resultCode.toString(), resultCode.getMessage(), null);
	}
}

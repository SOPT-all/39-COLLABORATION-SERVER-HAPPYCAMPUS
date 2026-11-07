package org.sopt.happycampus.common.response;

import org.sopt.happycampus.common.code.ResultCode;

public interface ApiResponse {
	int status();
	String code();
	String message();

	static <T> SuccessResponse<T> success(ResultCode resultCode, T data) {
		return SuccessResponse.of(resultCode, data);
	}

	static <T> SuccessResponse<T> success(ResultCode resultCode) {
		return SuccessResponse.of(resultCode);
	}

	static FailureResponse failure(ResultCode resultCode) {
		return FailureResponse.of(resultCode);
	}

	static FailureResponse failure(ResultCode resultCode, String message) {
		return FailureResponse.of(resultCode, message);
	}
}

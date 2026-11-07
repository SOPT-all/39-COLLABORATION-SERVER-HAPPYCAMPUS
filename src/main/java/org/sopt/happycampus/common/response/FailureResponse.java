package org.sopt.happycampus.common.response;

import org.sopt.happycampus.common.code.ResultCode;

public record FailureResponse(
	int status,
	String code,
	String message
) implements ApiResponse {
	public static FailureResponse of(ResultCode resultCode) {
		return new FailureResponse(resultCode.getStatus().value(), resultCode.toString(), resultCode.getMessage());
	}

	public static FailureResponse of(ResultCode resultCode, String message) {
		return new FailureResponse(resultCode.getStatus().value(), resultCode.toString(), message);
	}
}

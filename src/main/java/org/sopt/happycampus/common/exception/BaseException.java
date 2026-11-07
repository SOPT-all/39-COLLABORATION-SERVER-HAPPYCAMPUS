package org.sopt.happycampus.common.exception;

import org.sopt.happycampus.common.code.ResultCode;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
	private final ResultCode resultCode;

	protected BaseException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}
}

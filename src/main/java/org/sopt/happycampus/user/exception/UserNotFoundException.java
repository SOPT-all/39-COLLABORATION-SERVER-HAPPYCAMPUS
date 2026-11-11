package org.sopt.happycampus.user.exception;

import org.sopt.happycampus.common.exception.BaseException;
import org.sopt.happycampus.user.code.ErrorCode;

public class UserNotFoundException extends BaseException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}
}

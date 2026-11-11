package org.sopt.happycampus.folder.exception;

import org.sopt.happycampus.common.exception.BaseException;
import org.sopt.happycampus.folder.code.ErrorCode;

public class FolderNotFoundException extends BaseException {
	public FolderNotFoundException() {
		super(ErrorCode.FOLDER_NOT_FOUND);
	}
}

package org.sopt.happycampus.folder.exception;

import org.sopt.happycampus.common.exception.BaseException;
import org.sopt.happycampus.folder.code.ErrorCode;

public class FolderNameDuplicatedException extends BaseException {
	public FolderNameDuplicatedException() {
		super(ErrorCode.FOLDER_NAME_DUPLICATED);
	}
}

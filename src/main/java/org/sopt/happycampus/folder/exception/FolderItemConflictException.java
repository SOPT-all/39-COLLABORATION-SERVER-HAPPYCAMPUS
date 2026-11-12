package org.sopt.happycampus.folder.exception;

import org.sopt.happycampus.common.exception.BaseException;
import org.sopt.happycampus.folder.code.ErrorCode;

public class FolderItemConflictException extends BaseException {
	public FolderItemConflictException() {
		super(ErrorCode.FOLDER_ITEM_CONFLICT);
	}
}

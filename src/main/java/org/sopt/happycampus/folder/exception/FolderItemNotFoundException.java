package org.sopt.happycampus.folder.exception;

import org.sopt.happycampus.common.exception.BaseException;
import org.sopt.happycampus.folder.code.ErrorCode;

public class FolderItemNotFoundException extends BaseException {
	public FolderItemNotFoundException() {
		super(ErrorCode.FOLDER_ITEM_NOT_FOUND);
	}
}

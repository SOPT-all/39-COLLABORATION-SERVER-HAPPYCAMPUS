package org.sopt.happycampus.post.exception;

import org.sopt.happycampus.post.code.PostErrorCode;
import org.sopt.happycampus.common.exception.BaseException;

/**
 * 조회 대상 자료가 존재하지 않을 때 발생하는 예외 | exception: 오류가 발생했음을 알리는 객체
 */
public class PostNotFoundException extends BaseException{

    // 공통 예외 처리기에 전달할 자료 미존재 오류 코드 설정
    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_FOUND);
    }
}

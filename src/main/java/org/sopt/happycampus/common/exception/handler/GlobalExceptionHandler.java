package org.sopt.happycampus.common.exception.handler;

import org.sopt.happycampus.common.code.ErrorCode;
import org.sopt.happycampus.common.code.ResultCode;
import org.sopt.happycampus.common.exception.BaseException;
import org.sopt.happycampus.common.response.ApiResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse> handleBaseException(BaseException e) {
		return buildErrorResponse(e.getResultCode());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
		return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		return buildErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ApiResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
		return buildErrorResponse(ErrorCode.MISSING_REQUEST_HEADER);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		return buildErrorResponse(ErrorCode.INVALID_REQUEST_BODY);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		return buildErrorResponse(ErrorCode.INVALID_PARAMETER_TYPE);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());

		return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, message);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ApiResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
		String message = e.getParameterValidationResults().stream()
			.flatMap(result -> result.getResolvableErrors().stream())
			.findFirst()
			.map(MessageSourceResolvable::getDefaultMessage)
			.orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());

		return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException e) {
		String message = e.getConstraintViolations().stream()
			.findFirst()
			.map(ConstraintViolation::getMessage)
			.orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());

		return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, message);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse> handleNoResourceFoundException(NoResourceFoundException e) {
		return buildErrorResponse(ErrorCode.NOT_FOUND);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
		return buildErrorResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleException(Exception e) {
		log.error(e.getMessage(), e);
		return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ApiResponse> buildErrorResponse(ResultCode resultCode) {
		return ResponseEntity.status(resultCode.getStatus())
			.body(ApiResponse.failure(resultCode));
	}

	private ResponseEntity<ApiResponse> buildErrorResponse(ResultCode resultCode, String message) {
		return ResponseEntity.status(resultCode.getStatus())
			.body(ApiResponse.failure(resultCode, message));
	}
}

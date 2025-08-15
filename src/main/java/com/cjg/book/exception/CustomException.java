package com.cjg.book.exception;



import com.cjg.book.code.ResultCode;
import lombok.Getter;


@Getter
public class CustomException extends RuntimeException {
	
	private final ResultCode resultCode;
	
	public CustomException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

}

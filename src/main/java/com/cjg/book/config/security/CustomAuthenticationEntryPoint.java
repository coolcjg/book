package com.cjg.book.config.security;

import com.cjg.book.code.ResultCode;
import com.cjg.book.response.Response;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
			setResponse(response);
    }
	
	private void setResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().print(new Gson().toJson(Response.fail(ResultCode.BOOK_INVALID_PARAM)));
	}
	
}

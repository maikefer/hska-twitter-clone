package de.hska.lkit.sessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PathLoggerInterceptor extends HandlerInterceptorAdapter {

	
	Logger logger = LoggerFactory.getLogger(PathLoggerInterceptor.class);
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		logger.info("URI Access: " + request.getRequestURI());
		return true;
	}
	
	

}

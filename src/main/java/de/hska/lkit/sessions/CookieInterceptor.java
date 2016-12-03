package de.hska.lkit.sessions;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.hska.lkit.redis.repo.AuthRepository;

public class CookieInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AuthRepository redis;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
		Cookie[] cookies = req.getCookies();
		if (!ObjectUtils.isEmpty(cookies))
			for (Cookie cookie : cookies)
				if (cookie.getName().equals("auth")) {
					String auth = cookie.getValue();
					if (auth != null && redis != null) {
						String username = redis.isAuthValid(auth);
						if (username != null) {
							SessionSecurity.set(username, auth);
							return true;
						}
					}
				}
		
		// TODO: may causes bugs. Changing the pathPatterns in WebConfig.java may fixes the bugs.
		SessionSecurity.clear();
		return true;
	}
	
}

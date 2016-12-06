package de.hska.lkit.sessions;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.AuthRepository;

@Controller
public class LoginController {

	@Autowired
	private AuthRepository repository;
	private static final Duration TIMEOUT = Duration.ofMinutes(5);

	public boolean login(User user, HttpServletResponse response) {
		if (repository.auth(user.getUsername(), user.getPassword())) {
			String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
			Cookie cookie = new Cookie("auth", auth);
			response.addCookie(cookie);
			SessionSecurity.set(user.getUsername(), auth);
			return true;
		}

        // Login not valid
		return false;
	}

	@RequestMapping(value = "/logout")
	public String logout() {
		if (isLoggedin()) {
			String username = SessionSecurity.getName();
			repository.deleteAuth(username);
			SessionSecurity.clear();
		}

		return "redirect:/login";
	}

	public boolean isLoggedin() {
		return repository.isAuthValid(SessionSecurity.getToken()).equals(SessionSecurity.getName());
	}
}

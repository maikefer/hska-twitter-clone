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

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("user") @Valid User user, HttpServletResponse response, Model model) {
		if (repository.auth(user.getUsername(), user.getPassword())) {
			String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
			Cookie cookie = new Cookie("auth", auth);
			response.addCookie(cookie);
			model.addAttribute("user", user.getUsername());
			SessionSecurity.set(user.getUsername(), auth);
			return "users/" + user.getUsername();
		}
		model.addAttribute("user", new User());
		return "login";
	}
		
	// TODO: where do we go after logout?
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout() {
		if (isLoggedin(SessionSecurity.getName())) {
			String username = SessionSecurity.getName();
			repository.deleteAuth(username);
			SessionSecurity.clear();
		}
		return "redirect:/login";
	}
	
	public boolean isLoggedin(String username) {
		return repository.isAuthValid(SessionSecurity.getToken()).equals(SessionSecurity.getName());
	}
}

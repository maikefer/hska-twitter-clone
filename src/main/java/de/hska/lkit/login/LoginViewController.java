package de.hska.lkit.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by bob on 20/10/2016.
 */

@Controller
public class LoginViewController {

	@RequestMapping(value = "/login")
	public String showLoginView() {

		return "login";
	}
}

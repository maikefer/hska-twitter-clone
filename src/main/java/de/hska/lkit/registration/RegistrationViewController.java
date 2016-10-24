package de.hska.lkit.registration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Maike on 24/10/2016.
 */

@Controller
public class RegistrationViewController {

	@RequestMapping(value = "/registration")
	public String showRegistrationView() {
		return "registration";
	}

}
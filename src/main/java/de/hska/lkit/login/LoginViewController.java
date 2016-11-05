package de.hska.lkit.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by bob on 20/10/2016.
 */


@Controller
public class LoginViewController {
	
	private Logger logger = LoggerFactory.getLogger(LoginViewController.this.getClass());

    @RequestMapping(value = "/login")
    public String showLoginView() {
    	logger.info("LoginView called");
        return "login";
    }
}


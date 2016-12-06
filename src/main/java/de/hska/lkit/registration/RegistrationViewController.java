package de.hska.lkit.registration;

import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;


/**
 * Created by Maike on 24/10/2016.
 */

@Controller
public class RegistrationViewController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LoginController loginController;

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String showRegistrationView(Model model) {
	    model.addAttribute("user", new User());

        return "registration";
	}

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String showRegistrationView(@ModelAttribute("user") User userForm, HttpServletResponse response, Model model) {

	    if (userForm.getUsername() != null && userForm.getUsername().length() > 0)
            if (userForm.getEmail() != null) // TODO: validate email
                if (userForm.getPassword() != null && userForm.getPassword().length() > 0) {
	                // Check if user already exists
                    if (userRepo.isUsernameAvailable(userForm.getUsername())) {
                        model.addAttribute("error", "This username is already taken. Try a new one");
                        return "registration";
                    }

                    // Save user
                    userRepo.saveUser(userForm);

                    // Log user in
                    loginController.login(userForm, response);

                    // redirect to home page
                    return "redirect:/";
                }

        model.addAttribute("error", "Provided data not valid");
        return "registration";
    }

}

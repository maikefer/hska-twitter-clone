package de.hska.lkit.registration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.elasticsearch.model.EsUser;
import de.hska.lkit.elasticsearch.repo.ESUserRepository;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.LoginController;


/**
 * Created by Maike on 24/10/2016.
 */

@Controller
public class RegistrationViewController {

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private ESUserRepository esUserRepository;

    @Autowired
    private LoginController loginController;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationView(Model model) {
        model.addAttribute("user", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String showRegistrationView(@ModelAttribute("user") User userForm, HttpServletResponse response,
                                       Model model) {

        if (!isValidUsername(userForm.getUsername())) {
            model.addAttribute("error",
                "Username is not valid. The Username can only contain Letters and Numbers.");
            return "registration";
        }

        if (!isValidEmail(userForm.getEmail())) {  //todo validate mail
            model.addAttribute("error", "Please type in a valid E-Mail Address");
            return "registration";
        }
        if (!isValidPassword(userForm.getPassword())) {
            model.addAttribute("error", "Please type in a correct Password");
            return "registration";
        }
        if (!userRepo.isUsernameAvailable(userForm.getUsername())) {
            model.addAttribute("error", "This username is already taken. Try a new one");
            return "registration";
        }

        userRepo.saveUser(userForm);
        esUserRepository.save(new EsUser(userForm));
        loginController.login(userForm, response);
        return "redirect:/"; // redirect to home page
    }

    private boolean isValidEmail(String email) {
        return email != null && email.length() > 0
            && email.matches("[a-zA-Z0-9-_.]+[@][a-zA-Z0-9-_.]+[.][a-zA-Z0-9-_.]+") ;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() > 0 && !password.equals(" ");
    }

    /**
     * The Username is only allowed to contain a-z, A-Z and 0-9.
     * No special characters, no ä, ü, ö
     *
     * @param username The string that will be checked
     * @return If the sting is a valid username
     */
    private boolean isValidUsername(String username) {
        return username != null && username.length() > 0 && username.matches("[a-zA-Z0-9]+");
    }

}

package de.hska.lkit.user;

import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.SessionSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class UserViewController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/users/{username}")
    public String showUser(@PathVariable("username") String username, Model model) {

        // Add current user
        model.addAttribute("currentUser", userRepository.findUser(SessionSecurity.getName()));

        // Get requested user
        User user = userRepository.findUser(username);
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            // User not found
            String errorMessage = "User '" + username + "' not found!";
            model.addAttribute("errorMessage", errorMessage);
            return "error";
        }

        model.addAttribute("isSelf", SessionSecurity.getName().equals(user.getUsername()));
        return "user";
    }
}

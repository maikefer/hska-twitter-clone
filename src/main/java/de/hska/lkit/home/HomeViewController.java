package de.hska.lkit.home;

import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.SessionSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class HomeViewController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/")
    public String showHomeView(Model model) {

        // Get the current user
        User user = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("user", user);

        model.addAttribute("currentUser", user);
        model.addAttribute("isSelf", true);
        return "home";
    }
}


package de.hska.lkit.login;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepositroy;
import de.hska.lkit.sessions.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import static java.lang.Math.min;

/**
 * Created by bob on 20/10/2016.
 */

@Controller
public class LoginViewController {

    @Autowired
    private LoginController loginController;

    @Autowired
    private PostRepositroy postRepository;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginView(Model model) {
        model.addAttribute("user", new User());

        // Add some post to the view
        List<Post> globalPosts = postRepository.findAllPosts();
        List<Post> posts = globalPosts.subList(0, min(globalPosts.size(), 3));
        model.addAttribute("posts", posts);

		return "login";
	}

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String showLoginView(@ModelAttribute("user") User userForm, HttpServletResponse response, Model model) {

        if (userForm.getUsername() != null)
            if (userForm.getPassword() != null) {
                if (loginController.login(userForm, response)) {
                    // Redirect user to home
                    return "redirect:/";
                }
            }

        model.addAttribute("error", "Password or username is wrong.");
        return "login";
    }
}

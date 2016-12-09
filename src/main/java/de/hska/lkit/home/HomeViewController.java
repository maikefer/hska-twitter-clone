package de.hska.lkit.home;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepository;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.SessionSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class HomeViewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @RequestMapping(value = "/")
    public String showHomeView(Model model) {

        // Get the current user
        User user = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("user", user);

        // Followers Count
        model.addAttribute("followingCnt", userRepository.findFollowers(user.getUsername()).size());
        model.addAttribute("followerCnt", userRepository.findFollowing(user.getUsername()).size());

        model.addAttribute("currentUser", user);
        model.addAttribute("isSelf", true);

        List<Post> privatePosts = postRepository.timelineOfUser(user.getUsername());
        List<Post> globalPosts = new ArrayList<>(postRepository.findAllPosts());

        model.addAttribute("PostListGlobal", globalPosts);
        model.addAttribute("PostListPrivate", privatePosts);

        return "home";
    }

}

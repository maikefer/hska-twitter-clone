package de.hska.lkit.home;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepositroy;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.SessionSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class HomeViewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepositroy postRepository;

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
        List<Object> globalPosts = new ArrayList<>(postRepository.findAllPosts().values());

        model.addAttribute("PostListGlobal", globalPosts);
        model.addAttribute("PostListPrivate", privatePosts);

        return "home";
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String createPost(@ModelAttribute("postText") String postText, HttpServletRequest request, Model model) {

        User user = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);
        model.addAttribute("isSelf", true);
        model.addAttribute("followingCnt", userRepository.findFollowers(user.getUsername()).size());
        model.addAttribute("followerCnt", userRepository.findFollowing(user.getUsername()).size());

        Post post = new Post(postText, user.getUsername());
        postRepository.savePost(post);
        return "redirect:/";
    }

}

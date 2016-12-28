package de.hska.lkit.search;

import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.SessionSecurity;
import de.hska.lkit.user.Follower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Maike on 12.12.2016.
 */
@Controller
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchUser(SearchTerm searchTerm, BindingResult bindingResult, Model model){
        User user = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);
        model.addAttribute("isSelf", true);

        String userName = searchTerm.searchName;

        model.addAttribute("search", "\'" + userName + "\'");

        Set<String> foundUserNames = userRepository.searchUser(userName);

        model.addAttribute("amountResults", foundUserNames.size());

        List<Follower> listFollower = foundUserNames.stream()
            .map(username -> new Follower(username, userRepository.isFollower(username, user.getUsername())))
            .collect(Collectors.toList());
        model.addAttribute("listFollowing", listFollower);

        return "search";
    }
}

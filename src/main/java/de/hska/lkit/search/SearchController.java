package de.hska.lkit.search;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.elasticsearch.model.EsPost;
import de.hska.lkit.elasticsearch.model.EsUser;
import de.hska.lkit.elasticsearch.repo.ESPostRepository;
import de.hska.lkit.elasticsearch.repo.ESUserRepository;
import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepository;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.sessions.SessionSecurity;
import de.hska.lkit.user.Follower;

/**
 * Created by Maike on 12.12.2016.
 */
@Controller
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ESUserRepository esUserRepository;
    
    @Autowired
    private ESPostRepository esPostRepository;
    
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchUser(SearchTerm searchTerm, BindingResult bindingResult, Model model){
        User user = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);
        model.addAttribute("isSelf", true);

        String queryString = searchTerm.searchName;

        model.addAttribute("search", "\'" + queryString + "\'");

        //TODO: Fix query of ESUserRepository
        List<EsUser> foundUserNames = esUserRepository.findByUsernameLike("*" + queryString);
        
        esUserRepository.findAll().forEach( u -> logger.info("User: {}", u.getUsername()));

        model.addAttribute("amountResultsUsers", foundUserNames.size());

        List<Follower> listFollower = foundUserNames.stream()
        	.map( esUser -> esUser.getUsername())
            .map(username -> new Follower(username, userRepository.isFollower(username, user.getUsername())))
            .collect(Collectors.toList());
        model.addAttribute("listFollowing", listFollower);
        
        
        List<EsPost> foundPostIds = esPostRepository.findByMessageLike("*" + queryString);
        
        esUserRepository.findAll().forEach( u -> logger.info("User: {}", u.getUsername()));

        model.addAttribute("amountResultsPosts", foundPostIds.size());

        List<Post> listPosts = foundPostIds.stream()
        	.map( esPost -> esPost.getId())
            .map( id -> postRepository.findPost(id))
            .collect(Collectors.toList());
        model.addAttribute("PostListGlobal", listPosts);
        

        return "search";
    }
}

package de.hska.lkit;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepositroy;
import de.hska.lkit.redis.repo.UserRepository;


/**
 * @author knad0001
 *
 */
@Controller
public class UserController {

	private final UserRepository userRepository;
	
	private final PostRepositroy postRepository;

	@Autowired
	public UserController(UserRepository userRepository, PostRepositroy postRepositroy) {
		this.userRepository = userRepository;
		this.postRepository = postRepositroy;
	}

	/**
	 * list all users
	 * 
	 * @param model
	 * 
	 * @return
	 */
	@RequestMapping(value = "/posts", method = RequestMethod.GET)
	public String getPostTest(Model model) {
		Post post = new Post(""+ new Date().getTime(), LocalDateTime.now(), "Test message", "1");
		postRepository.savePost(post);
		
		Map<Object, Object> retrievedPosts = postRepository.findAllPosts();
		model.addAttribute("posts", retrievedPosts);
		return "posts";
	}
	
	/**
	 * list all users
	 * 
	 * @param model
	 * 
	 * @return
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String getAllUsers(Model model) {
		Map<Object, Object> retrievedUsers = userRepository.findAllUsers();
		model.addAttribute("users", retrievedUsers);
		return "users";
	}

	/**
	 * get information for user with username
	 * 
	 * @param username
	 *            username to find
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	public String getOneUsers(@PathVariable("username") String username, Model model) {
		User found = userRepository.findUser(username);

		model.addAttribute("userFound", found);
		return "oneUser";
	}

	/**
	 * redirect to page to add a new user
	 * 
	 * @return
	 */
	@RequestMapping(value = "/adduser", method = RequestMethod.GET)
	public String addUser(@ModelAttribute User user) {
		return "newUser";
	}

	/**
	 * add a new user, adds a list of all users to model
	 * 
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/adduser", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute User user, Model model) {

		userRepository.saveUser(user);
		model.addAttribute("message", "User successfully added");

		Map<Object, Object> retrievedUsers = userRepository.findAllUsers();

		model.addAttribute("users", retrievedUsers);
		return "users";
	}

}

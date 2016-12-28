package de.hska.lkit.user;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepository;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.search.SearchTerm;
import de.hska.lkit.sessions.SessionSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class  UserViewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private final int postsPerPage = 10;

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/users/{username}")
    public String showUser(@PathVariable("username") String username, Model model) {
        return generatePagedUserProfil(username, model, 0);
    }

    private String generatePagedUserProfil(@PathVariable("username") String username, Model model, int pageNumber) {
        // Add current user
        User currentUser = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("currentUser", currentUser);

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

        if (!SessionSecurity.getName().equals(user.getUsername())) { // Another user
            boolean isFollowing = this.following(currentUser.getUsername(), user.getUsername());
            model.addAttribute("isFollowing", isFollowing);
        } else {
            model.addAttribute("isFollowing", false);
        }

        // pagination
        model.addAttribute("nextPageNo", pageNumber + 1);

        if (pageNumber == 0) {
            // hide previous
            model.addAttribute("displayPrev", "display:none");
        } else {
            model.addAttribute("displayPrev", "display:inline");
        }

        long maxPageNumber = postRepository.findPostsByUserSize(username) / postsPerPage;
        if (pageNumber == maxPageNumber) {
            // hide next
            model.addAttribute("displayNext", "display:none");
        } else {
            model.addAttribute("displayNext", "display:inline");
        }

        int firstPost = pageNumber * postsPerPage;
        int count = postsPerPage - 1;
        List<Post> posts = postRepository.findPostsByUserPaged(username, firstPost, count);
        model.addAttribute("PostListUser", posts);

        // Follower
        Set<String> follower = userRepository.findFollowers(user.getUsername());
        Set<String> following = userRepository.findFollowing(user.getUsername());

        // Follower Count
        model.addAttribute("followingCnt", follower.size());
        model.addAttribute("followerCnt", following.size());

        model.addAttribute("searchTerm", new SearchTerm());
        model.addAttribute("isSelf", SessionSecurity.getName().equals(user.getUsername()));
        return "user";
    }

    @RequestMapping(value = "/users/{username}/{page}")
    public String showUserOnPage(@PathVariable("username") String username, @PathVariable("page") int page, Model model) {
        return generatePagedUserProfil(username, model, page);
    }

    @RequestMapping(value = "/users/{username}/previous/{page}")
    public String previousPage(@PathVariable("username") String username, @PathVariable("page") int page, Model model) {
        return "redirect:/users/" + username + "/" + (page - 2);
    }

    @RequestMapping(value = {"/follow/{username}", "/follow/{username}/{user}/{tab}"})
    public String followUser(@PathVariable("username") String username, @PathVariable(value = "user", required = false) String user, @PathVariable(value = "tab", required = false) String tab) {

        // Get users
        User currentUser = userRepository.findUser(SessionSecurity.getName());
        User userToFollow = userRepository.findUser(username);

        userRepository.startFollowUser(currentUser.getUsername(), userToFollow.getUsername());

        if (tab != null && user != null)
            return "redirect:/users/" + user + "/" + tab;

        return "redirect:/users/" + username;
    }

    @RequestMapping(value = { "/unfollow/{username}", "/unfollow/{username}/{user}/{tab}" })
    public String unFollowUser(@PathVariable("username") String username, @PathVariable(value = "user", required = false) String user, @PathVariable(value = "tab", required = false) String tab) {

        // Get users
        User currentUser = userRepository.findUser(SessionSecurity.getName());
        User userToUnFollow = userRepository.findUser(username);

        userRepository.stopFollowUser(currentUser.getUsername(), userToUnFollow.getUsername());

        if (tab != null && user != null)
            return "redirect:/users/" + user + "/" + tab;

        return "redirect:/users/" + username;
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/users/{username}/follower")
    public String showFollower(@PathVariable("username") String username, Model model) {

        // Get currentUser
        User currentUser = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("currentUser", currentUser);

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

        if (!SessionSecurity.getName().equals(user.getUsername())) { // Another user
            boolean isFollowing = this.following(currentUser.getUsername(), user.getUsername());
            model.addAttribute("isFollowing", isFollowing);
        } else {
            model.addAttribute("isFollowing", false);
        }

        // Follower
        Set<String> follower = userRepository.findFollowers(user.getUsername());
        Set<String> following = userRepository.findFollowing(user.getUsername());

        // Follower Count
        model.addAttribute("followingCnt", follower.size());
        model.addAttribute("followerCnt", following.size());

        // Following list
        List<Follower> listFollowing = new ArrayList<>();
        for (String name : follower)
            listFollowing.add(new Follower(name, this.following(currentUser.getUsername(), name)));
        model.addAttribute("listFollowing", listFollowing);

        // Follower list
        List<Follower> listFollower = new ArrayList<>();
        for (String name : following)
            listFollower.add(new Follower(name, this.following(currentUser.getUsername(), name)));
        model.addAttribute("listFollower", listFollower);

        model.addAttribute("searchTerm", new SearchTerm());
        model.addAttribute("isSelf", SessionSecurity.getName().equals(user.getUsername()));
        return "follower";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/users/{username}/following")
    public String showFollowing(@PathVariable("username") String username, Model model) {

        // Get currentUser
        User currentUser = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("currentUser", currentUser);

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

        if (!SessionSecurity.getName().equals(user.getUsername())) { // Another user
            boolean isFollowing = this.following(currentUser.getUsername(), user.getUsername());
            model.addAttribute("isFollowing", isFollowing);
        } else {
            model.addAttribute("isFollowing", false);
        }

        // Follower
        Set<String> follower = userRepository.findFollowers(user.getUsername());
        Set<String> following = userRepository.findFollowing(user.getUsername());

        // Follower Count
        model.addAttribute("followingCnt", follower.size());
        model.addAttribute("followerCnt", following.size());

        // Following list
        List<Follower> listFollowing = new ArrayList<>();
        for (String name : follower)
            listFollowing.add(new Follower(name, this.following(currentUser.getUsername(), name)));
        model.addAttribute("listFollowing", listFollowing);

        // Follower list
        List<Follower> listFollower = new ArrayList<>();
        for (String name : following)
            listFollower.add(new Follower(name, this.following(currentUser.getUsername(), name)));
        model.addAttribute("listFollower", listFollower);

        model.addAttribute("searchTerm", new SearchTerm());
        model.addAttribute("isSelf", SessionSecurity.getName().equals(user.getUsername()));
        return "following";
    }

    /**
     * Checks if username is following the otherUser
     * @param username
     * @param otherUser
     * @return
     */
    private boolean following(String username, String otherUser) {
        return userRepository.findFollowers(username).contains(otherUser);
    }
}

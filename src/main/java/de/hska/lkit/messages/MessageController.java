package de.hska.lkit.messages;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.repo.PostRepository;
import de.hska.lkit.redis.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by bob on 08.12.2016.
 */


@Controller
public class MessageController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/post")
    @SendTo("/topic/posts")
    public Post post(PostMessage postMessage) throws Exception {
        // Create and save post
        Post post = new Post(postMessage.getMessage(), postMessage.getUsername());
        postRepository.savePost(post);
        return post;
    }

    @RequestMapping(value = "/isfollower", method = RequestMethod.GET, produces = "application/json", params = { "username", "follower" })
    @ResponseBody
    public FollowerResponse isFollower(@RequestParam("username") String of, @RequestParam("follower") String name) {
        // Check if the user is follower of another one
        boolean isFollower = userRepository.isFollower(name, of);

        return new FollowerResponse(isFollower);
    }
}

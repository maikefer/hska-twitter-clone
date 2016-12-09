package de.hska.lkit.messages;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


/**
 * Created by bob on 08.12.2016.
 */


@Controller
public class MessageController {

    @Autowired
    private PostRepository postRepository;

    @MessageMapping("/post")
    @SendTo("/topic/posts")
    public Post post(PostMessage postMessage) throws Exception {
        // Create and save post
        Post post = new Post(postMessage.getMessage(), postMessage.getUsername());
        postRepository.savePost(post);
        return post;
    }
}

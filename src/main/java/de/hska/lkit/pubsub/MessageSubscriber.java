package de.hska.lkit.pubsub;

import de.hska.lkit.redis.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Stack;

/**
 * Created by Timo on 10.01.2017.
 */
@Service
public class MessageSubscriber implements MessageListener {

    public static Stack<Post> recentPosts = new Stack<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SimpMessagingTemplate template;

    MessageSubscriber(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void onMessage(Message message, byte[] pattern) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(message.getBody());
            ObjectInputStream ois = new ObjectInputStream(bis);
            Post post = (Post) ois.readObject();
            recentPosts.push(post);
            logger.info("received post: " + post.toString());

            // Send to all connected clients
            this.sendPost(post);
        } catch (Exception e) {
            logger.error("can't convert message to Post, message: " + message.toString());
        }
    }

    private void sendPost(Post post) throws Exception {
        System.out.println("Sending to all clients");
        this.template.convertAndSend("/topic/posts", post);
    }
}


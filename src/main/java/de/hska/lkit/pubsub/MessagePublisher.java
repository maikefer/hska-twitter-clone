package de.hska.lkit.pubsub;

import de.hska.lkit.redis.model.Post;
import org.codehaus.groovy.runtime.callsite.PogoMetaMethodSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * Created by Timo on 10.01.2017.
 */
public class MessagePublisher {

    @Autowired
    private RedisTemplate<String, Post> redisPostTemplate;

    @Autowired
    private ChannelTopic topic;

    public MessagePublisher() {

    }

    public MessagePublisher(RedisTemplate<String, Post> template, ChannelTopic topic) {
        redisPostTemplate = template;
        this.topic = topic;
    }

    public void publish(Post post){
        redisPostTemplate.convertAndSend(topic.getTopic(), post);
    }
}

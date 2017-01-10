package de.hska.lkit.pubsub;

import de.hska.lkit.redis.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Created by Timo on 10.01.2017.
 */
@Configuration
public class PubSubConfiguration {

    public static final String postChannelName = "updates:post";

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    RedisTemplate<String, Post> redisPostTemplate;

    SimpMessagingTemplate template;

    @Autowired
    public PubSubConfiguration(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new MessageSubscriber(this.template));
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container
            = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    MessagePublisher redisPublisher() {
        return new MessagePublisher(redisPostTemplate, topic());
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(postChannelName);
    }
}


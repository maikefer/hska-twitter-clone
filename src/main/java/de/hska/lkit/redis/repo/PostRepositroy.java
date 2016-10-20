package de.hska.lkit.redis.repo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.redis.model.Post;

@Repository
public class PostRepositroy {
	
	private static final String KEY_HASH_ALL_POSTS = "hash:all:post";
	

	/**
	 * to save user data as object
	 */
	private RedisTemplate<String, Post> redisTemplate;


	private RedisAtomicLong postid;
	
	@Autowired
	public PostRepositroy(RedisTemplate<String, Post> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.postid = new RedisAtomicLong("next_post_id", stringRedisTemplate.getConnectionFactory());
	}
	
	/**
	 * Save a post
	 * @param post
	 */
	public void savePost(Post post) {
		// generate a unique id
		String id = String.valueOf(postid.incrementAndGet());
		post.setId(id);
				
		// add id of post to list of all posts
		this.redisTemplate.opsForHash().put(KEY_HASH_ALL_POSTS, post.getId(), post);
		
		//TODO: Add id of post to users timeline and followers timeline 		
	}
	
	/**
	 * returns a list of all users
	 * 
	 * @return
	 */
	public Map<Object, Object> findAllPosts() {
		return this.redisTemplate.opsForHash().entries(KEY_HASH_ALL_POSTS);
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Post findPost(String id) {
		return (Post) this.redisTemplate.opsForHash().get(KEY_HASH_ALL_POSTS, id);
	}

}

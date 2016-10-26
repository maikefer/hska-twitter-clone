package de.hska.lkit.redis.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.redis.model.Post;

@Repository
public class PostRepositroy {
	
	private UserRepository userRepository;
	
	private RedisTemplate<String, Post> redisPost;
	private StringRedisTemplate redis;
	private RedisAtomicLong postid;
	
	//Logger
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public PostRepositroy(RedisTemplate<String, Post> redisPost, StringRedisTemplate stringRedisTemplate, UserRepository userRepository) {
		this.redisPost = redisPost;
		this.redis = stringRedisTemplate;		
		this.userRepository = userRepository;
	}
	
	@PostConstruct
	private void init() {
		this.postid = new RedisAtomicLong(KeyUtils.nextPostId(), redis.getConnectionFactory());
	}
	
	/**
	 * Save a post
	 * @param post
	 */
	public void savePost(Post post) {
		// generate a unique id
		String id = String.valueOf(postid.incrementAndGet());
		post.setId(id);
		
		this.redisPost.opsForHash().put( KeyUtils.postAllHash(), id, post);		
				
		// add id of post to list of all posts
		this.redis.opsForSet().add(KeyUtils.postAll(), id);
		
		//add post to the lost of posts by this user
		this.redis.opsForList().leftPush( KeyUtils.postOfUser(post.getUser()), id);
		
 		// add post to timeline of the user himself and his followers
		for(String follower : userRepository.findFollowers(post.getUser())) {
			this.redis.opsForList().leftPush( KeyUtils.timeline(follower), id);
		}		
		this.redis.opsForList().leftPush( KeyUtils.timeline(post.getUser()), id);
		
		logger.info("Stored 'post:{}' of 'user:{}'", id, post.getUser());
	}
	
	/**
	 * returns a list of all users
	 * 
	 * @return
	 */
	public Map<Object, Object> findAllPosts() {
		return this.redisPost.opsForHash().entries( KeyUtils.postAllHash() );
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Post findPost(String id) {
		Object obj = this.redisPost.opsForHash().get( KeyUtils.postAllHash(), id);
		return (Post) obj;
	}
	
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public List<Post> findPostsByUser(String username) {
		List<Post> posts = new ArrayList<>();
		
		List<String> postIDs = this.redis.opsForList().range( KeyUtils.postOfUser(username), 0, -1);
				
		for(String id : postIDs) {
			posts.add( findPost(id));
		}
		
		return posts;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public List<Post> timelineOfUser(String username) {
		List<Post> posts = new ArrayList<>();
		
		List<String> postIDs = this.redis.opsForList().range( KeyUtils.timeline(username), 0, -1);
				
		for(String id : postIDs) {
			posts.add( findPost(id));
		}
		
		return posts;
	}
	
	
	

}

package de.hska.lkit.redis.repo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import de.hska.lkit.pubsub.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.redis.model.Post;

@Repository
public class PostRepository {

	private UserRepository userRepository;

	private RedisTemplate<String, Post> redisPost;
	private StringRedisTemplate redis;
	private RedisAtomicLong postid;
	private MessagePublisher messagePublisher;

	//Logger
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public PostRepository(RedisTemplate<String, Post> redisPost, StringRedisTemplate stringRedisTemplate, UserRepository userRepository, MessagePublisher messagePublisher) {
		this.redisPost = redisPost;
		this.redis = stringRedisTemplate;
		this.userRepository = userRepository;
		this.messagePublisher = messagePublisher;
	}

	@PostConstruct
	private void init() {
		this.postid = new RedisAtomicLong(KeyUtils.nextPostId(), redis.getConnectionFactory());
	}

	/**
	 * Save a given post
	 * @param post The Post to be saved
	 */
	public void savePost(Post post) {
		// generate a unique id
		String id = String.valueOf(postid.incrementAndGet());
		post.setId(id);

		this.redisPost.opsForHash().put( KeyUtils.postAllHash(), id, post);

		// add id of post to list of all posts
		this.redis.opsForList().leftPush(KeyUtils.postAll(), id);

		//add post to the lost of posts by this user
		this.redis.opsForList().leftPush( KeyUtils.postOfUser(post.getUser()), id);

 		// add post to timeline of the user himself and his followers
		for(String follower : userRepository.findFollowers(post.getUser())) {
			this.redis.opsForList().leftPush( KeyUtils.timeline(follower), id);
		}
		this.redis.opsForList().leftPush( KeyUtils.timeline(post.getUser()), id);
        messagePublisher.publish(post);

		logger.info("Stored 'post:{}' of 'user:{}'", id, post.getUser());

	}

	/**
	 * Returns all posts ever posted in the wide universe
	 * @deprecated Use paged functions instead {@link #findAllPostsPaginaton(int, int)}
	 * @return A List of Posts
	 */
	@Deprecated
	public List<Post> findAllPosts() {
		List<Post> posts = new ArrayList<>();

		List<String> postIDs = this.redis.opsForList().range( KeyUtils.postAll(), 0, -1);

		for(String id : postIDs) {
			posts.add( findPost(id));
		}

		return posts;
	}


	/**
	 * Returns the number of all posts
	 * @return The number of Posts
	 */
	public long findAllPostsSize() {
		return this.redis.opsForList().size( KeyUtils.postAll());
	}

	/**
	 * Returns a subset of all Posts
	 * @param start The start index, starts with 0
	 * @param count The number of posts to return
	 * @return A List of Posts
	 */
	public List<Post> findAllPostsPaged(int start, int count) {
		List<Post> posts = new ArrayList<>();

		List<String> postIDs = this.redis.opsForList().range( KeyUtils.postAll(), start, start + count);

		for(String id : postIDs) {
			posts.add( findPost(id));
		}

		return posts;
	}


	/**
	 * Returns the post with the given ID
	 * @param id
	 * @return a Post object
	 */
	public Post findPost(String id) {
		Object obj = this.redisPost.opsForHash().get( KeyUtils.postAllHash(), id);
		return (Post) obj;
	}


	/**
	 * Returns all Posts of the given user
	 * @param username
	 * @return List of Posts
	 */
	@Deprecated
	public List<Post> findPostsByUser(String username) {
		List<Post> posts = new ArrayList<>();

		List<String> postIDs = this.redis.opsForList().range( KeyUtils.postOfUser(username), 0, -1);

		for(String id : postIDs) {
			posts.add( findPost(id));
		}

		return posts;
	}


	/**
	 * The number of posts by a given user
	 * @param username
	 * @return
	 */
	public Long findPostsByUserSize(String username) {
		return this.redis.opsForList().size( KeyUtils.postOfUser(username));
	}


	/**
	 * Returns a subset of all Posts by the given user
	 * @param username
	 * @param start The start index, starts with 0
	 * @param count returns count + 1 posts. If there are less posts that the count, all posts will be returned.
	 * @return A List of Post
	 */
	public List<Post> findPostsByUserPaged(String username, int start, int count) {
		List<Post> posts = new ArrayList<>();

		List<String> postIDs = this.redis.opsForList().range( KeyUtils.postOfUser(username), start, start + count);

		for(String id : postIDs) {
			posts.add( findPost(id));
		}

		return posts;
	}

	/**
	 * Returns all posts for the given user to be shown
	 * @deprecated Use paged version instead {@link #timelineOfUserPaged(String, int, int)}
	 * @param username
	 * @return List of Posts
	 */
	@Deprecated
	public List<Post> timelineOfUser(String username) {
		List<Post> posts = new ArrayList<>();

		List<String> postIDs = this.redis.opsForList().range( KeyUtils.timeline(username), 0, -1);

		for(String id : postIDs) {
			posts.add( findPost(id));
		}
		return posts;
	}


	/**
	 * The number of posts of the timeline for the given user
	 * @param username
	 * @return
	 */
	public long timelineOfUserSize(String username) {
		return this.redis.opsForList().size( KeyUtils.timeline(username));
	}


	/**
	 * Returns all posts for the given user to be shown
	 * @param username
	 * @return List of Posts
	 */
	public List<Post> timelineOfUserPaged(String username, int start, int count) {
		List<Post> posts = new ArrayList<>();

		List<String> postIDs = this.redis.opsForList().range( KeyUtils.timeline(username), start, start + count);

		for(String id : postIDs) {
			posts.add( findPost(id));
		}
		return posts;
	}
}

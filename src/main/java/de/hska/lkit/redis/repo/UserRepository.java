package de.hska.lkit.redis.repo;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.redis.model.User;



/**
 * @author knad0001
 * @author essigt
 */
@Repository
public class UserRepository {

	/**
	 * 
	 */
	private static final String KEY_FOR_ALL_USERS = "all:users";

	private static final String KEY_HASH_ALL_USERS = "hash:all:user";

	/**
	 * to generate unique ids for user
	 */
	private RedisAtomicLong userid;

	/**
	 * to save data in String format
	 */
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * to save user data as object
	 */
	private RedisTemplate<String, User> redisTemplate;

	/**
	 * hash operations for stringRedisTemplate
	 */
	private HashOperations<String, String, String> srt_hashOps;

	private SetOperations<String, String> setOps;

	/**
	 * hash operations for redisTemplate
	 */
	private HashOperations<String, Object, Object> rt_hashOps;

	@Autowired
	public UserRepository(RedisTemplate<String, User> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.userid = new RedisAtomicLong("userid", stringRedisTemplate.getConnectionFactory());
	}

	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		setOps = stringRedisTemplate.opsForSet();

		rt_hashOps = redisTemplate.opsForHash();

	}

	/**
	 * save user to repository
	 * 
	 * @param user
	 */
	public void saveUser(User user) {
		// generate a unique id
		String id = String.valueOf(userid.incrementAndGet());

		user.setId(id);

		// to show how objects can be saved
		// be careful, if username already exists it's not added another time
		String key = "user:" + user.getUsername();
		srt_hashOps.put(key, "id", id);
		srt_hashOps.put(key, "firstName", user.getFirstname());
		srt_hashOps.put(key, "lastName", user.getLastname());
		srt_hashOps.put(key, "username", user.getUsername());
		srt_hashOps.put(key, "password", user.getPassword());

		setOps.add(KEY_FOR_ALL_USERS, key);

		// to show how objects can be saved
		rt_hashOps.put(KEY_HASH_ALL_USERS, key, user);

	}

	/**
	 * returns a list of all users
	 * 
	 * @return
	 */
	public Map<Object, Object> findAllUsers() {
		return rt_hashOps.entries(KEY_HASH_ALL_USERS);
	}

	/**
	 * find the user with username
	 * 
	 * @param username
	 * @return
	 */
	public User findUser(String username) {
		User user = new User();
		String key = "user:" + username;

		if (setOps.isMember(KEY_FOR_ALL_USERS, key)) {
			user.setId(srt_hashOps.get(key, "id"));
			user.setFirstname(srt_hashOps.get(key, "firstName"));
			user.setLastname(srt_hashOps.get(key, "lastName"));
			user.setUsername(srt_hashOps.get(key, "username"));
			user.setPassword(srt_hashOps.get(key, "password"));
		} else
			user = null;
		return user;
	}

}
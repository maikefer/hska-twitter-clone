package de.hska.lkit.redis.repo;

import static de.hska.lkit.redis.repo.KeyUtils.nextUserId;
import static de.hska.lkit.redis.repo.KeyUtils.user;
import static de.hska.lkit.redis.repo.KeyUtils.userAll;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
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

	private RedisAtomicLong userid;
	private StringRedisTemplate stringRedisTemplate;	
	private HashOperations<String, String, String> hashOps;
	private SetOperations<String, String> setOps;


	@Autowired
	public UserRepository(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;	
	}

	@PostConstruct
	private void init() {
		this.hashOps = this.stringRedisTemplate.opsForHash();
		this.setOps = this.stringRedisTemplate.opsForSet();
		this.userid = new RedisAtomicLong( nextUserId() , this.stringRedisTemplate.getConnectionFactory());
	}

	/**
	 * Checks if the username is still available
	 * @param username
	 * @return
	 */
	public boolean isUsernameAvailable(String username) {
		return !setOps.isMember(userAll(), user(username));
	}
	
	/**
	 * save user to repository
	 * 
	 * @param user
	 */
	public void saveUser(User user) {		
		if(user.getId() == null) {
			user.setId( "" + userid.incrementAndGet() );
		}

		String key = user(user.getUsername());
		hashOps.put(key, "id", user.getId());
		hashOps.put(key, "firstName", user.getFirstname());
		hashOps.put(key, "lastName", user.getLastname());
		hashOps.put(key, "username", user.getUsername());
		hashOps.put(key, "password", user.getPassword());

		setOps.add(userAll(), key);
	}
	
	/**
	 * Delete user from the repository
	 * 
	 * @param user
	 */
	public void deleteUser(User user) {		
		String key = user(user.getUsername());
		
		for(String property : hashOps.keys(key)) {
			hashOps.delete(key, property);
		}

		setOps.remove(userAll(), key);
	}

	/**
	 * Returns a Set of all usernames
	 * @return
	 */
	public Set<String> findAllUsers() {
		return setOps.members( userAll() );
	}

	/**
	 * Find the user with the given username
	 * 
	 * @param username
	 * @return The User object or null if no user with the given name exists
	 */
	public User findUser(String username) {
		User user = new User();
		String key = user(username);

		if (setOps.isMember(userAll(), key)) {
			user.setId(hashOps.get(key, "id"));
			user.setFirstname(hashOps.get(key, "firstName"));
			user.setLastname(hashOps.get(key, "lastName"));
			user.setUsername(hashOps.get(key, "username"));
			user.setPassword(hashOps.get(key, "password"));
		} else {
			user = null;
		}
		return user;
	}
	
	/**
	 * 
	 * @param username
	 * @param follower
	 */
	public void startFollowUser(String username, String follower) {
		String keyFollower = KeyUtils.follower(username);
		setOps.add(keyFollower, follower);
		
		String keyFollowing = KeyUtils.following(follower);
		setOps.add(keyFollowing, username);
	}
	
	/**
	 * 
	 * @param username
	 * @param follower
	 */
	public void stopFollowUser(String username, String follower) {
		String key = KeyUtils.follower(username);
		setOps.remove(key, follower);
		
		String keyFollowing = KeyUtils.following(follower);
		setOps.remove(keyFollowing, username);
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findFollowers(String username) {
		String key = KeyUtils.follower(username);
		return setOps.members(key);
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findFollowing(String username) {
		String key = KeyUtils.following(username);
		return setOps.members(key);
	}
	
	

}
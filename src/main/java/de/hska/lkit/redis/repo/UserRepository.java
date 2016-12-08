package de.hska.lkit.redis.repo;

import static de.hska.lkit.redis.repo.KeyUtils.nextUserId;
import static de.hska.lkit.redis.repo.KeyUtils.user;
import static de.hska.lkit.redis.repo.KeyUtils.userAll;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.redis.model.User;
import redis.clients.jedis.Jedis;



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
	private ZSetOperations<String, String> zsetOps;


	@Autowired
	public UserRepository(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@PostConstruct
	private void init() {
		this.hashOps = this.stringRedisTemplate.opsForHash();
		this.setOps = this.stringRedisTemplate.opsForSet();
		this.zsetOps = this.stringRedisTemplate.opsForZSet();
		this.userid = new RedisAtomicLong( nextUserId() , this.stringRedisTemplate.getConnectionFactory());
	}

	/**
	 * Checks if the username is still available(not taken jet)
	 * @param username
	 * @return
	 */
	public boolean isUsernameAvailable(String username) {
		return zsetOps.score(userAll(), username) == null;		
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
		hashOps.put(key, "username", user.getUsername());
		hashOps.put(key, "password", user.getPassword());
        hashOps.put(key, "email", user.getEmail());

		//setOps.add(userAll(), key);
        zsetOps.add(userAll(), user.getUsername(), 0.0);
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

		//setOps.remove(userAll(), key);
		zsetOps.remove(userAll(), user.getUsername());
	}

	/**
	 * Returns a Set of all usernames
	 * @return
	 */
	public long numberOfUsers() {
		return zsetOps.size( userAll() );
		
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

		
		if (zsetOps.score(userAll(), username) != null) {
			user.setId(hashOps.get(key, "id"));
			user.setEmail(hashOps.get(key, "email"));
			user.setUsername(hashOps.get(key, "username"));
			user.setPassword(hashOps.get(key, "password"));
		} else {
			user = null;
		}
		return user;
	}

	
	/**
	 * {@link http://stackoverflow.com/a/33865770/7043300}
	 * @param usernamePrefix
	 * @return
	 */
	public Set<String> searchUser(String usernamePrefix) {
		//Magicaly works not, too(dafuq?)
		return zsetOps.rangeByLex(userAll(), Range.range().gte(usernamePrefix).lte(usernamePrefix + "Z"));
		
		
		//Works
		//Jedis jedis = new Jedis();
		//return jedis.zrangeByLex(userAll(), "["+ usernamePrefix, "[" + usernamePrefix+"Z");		
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

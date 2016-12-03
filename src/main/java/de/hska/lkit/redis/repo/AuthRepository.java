package de.hska.lkit.redis.repo;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository {
	
	@Autowired
	private StringRedisTemplate redis;

	
	/**
	 * Checks if the auth-token is vaild
	 * 
	 * @param auth
	 * @return The username or null if the auth-token is not valid
	 */
	public String isAuthValid(String auth) {
		String username = redis.opsForValue().get( KeyUtils.auth(auth));
		if(username == null) { //No valid Auth found
			return null;
		} else {
			return username;
		}		
	}
	
	/**
	 * Checks if the username and password are correct
	 * @param username
	 * @param pass
	 * @return
	 */
	public boolean auth(String username, String pass) {
		String password = (String) redis.opsForHash().get(KeyUtils.user(username), "password");
		return password.equals(pass);
	}

	/**
	 * Authenticates the user for a given time
	 * @param username
	 * @param timeout
	 * @param tUnit
	 * @return
	 */
	public String addAuth(String username, long timeout, TimeUnit tUnit) {
		String auth = UUID.randomUUID().toString();
		
		redis.boundHashOps(KeyUtils.user(username)).put("auth", auth);
		redis.opsForValue().set(KeyUtils.auth(auth), username, timeout, tUnit);
		return auth;
	}
	
	/**
	 * Deletes the authentication of the given user
	 * @param uname
	 */
	public void deleteAuth(String username) {
		String auth = (String)redis.opsForHash().get(KeyUtils.user(username), "auth");
		redis.delete(KeyUtils.auth(auth));
	}
}
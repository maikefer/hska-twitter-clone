package de.hska.lkit.redis.repo;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository {
	
	@Autowired
	private StringRedisTemplate template;

	
	/**
	 * Checks if the auth-token is vaild
	 * TODO: Usefull to return the uid instead of a boolean value?
	 * @param auth
	 * @return
	 */
	public boolean authValid(String auth) {
		String uid = template.opsForValue().get("auth:" + auth + ":uid");
		if(uid == null) { //No valid Auth found
			return false;
		} else {
			return true;
		}		
	}
	
	/**
	 * Checks if the username and password are correct
	 * @param uname
	 * @param pass
	 * @return
	 */
	public boolean auth(String uname, String pass) {
		String uid = template.opsForValue().get("uname:" + uname + ":uid");
		
		BoundHashOperations<String, String, String> userOps = template.boundHashOps("uid:" + uid + ":user");
		
		return userOps.get("password").equals(pass);
	}

	/**
	 * Authenticates the user for a given time
	 * @param uname
	 * @param timeout
	 * @param tUnit
	 * @return
	 */
	public String addAuth(String uname, long timeout, TimeUnit tUnit) {
		String uid = template.opsForValue().get("uname:" + uname + ":uid");
		String auth = UUID.randomUUID().toString();
		
		template.boundHashOps("uid:" + uid + ":auth").put("auth", auth);
		template.expire("uid:" + uid + ":auth", timeout, tUnit);
		template.opsForValue().set("auth:" + auth + ":uid", uid, timeout, tUnit);
		
		return auth;
	}

	
	/**
	 * Deletes the authentication of the given user
	 * @param uname
	 */
	public void deleteAuth(String uname) {
		String uid = template.opsForValue().get("uname:" + uname + ":uid");
		String authKey = "uid:" + uid + ":auth";
		String auth = (String) template.boundHashOps(authKey).get("auth");
		
		List<String> keysToDelete = Arrays.asList(authKey, "auth:" + auth + ":uid");
		template.delete(keysToDelete);
	}
}
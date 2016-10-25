package de.hska.lkit.redis.repo;


/**
 * 
 * @author essigt
 *
 */
public abstract class KeyUtils {
	
	
	public static String user(String username) {
		return "user:" + username;
	}
	
	public static String follower(String username) {
		return "user:" + username + ":follower";
	}
	
	public static String following(String username) {
		return "user:" + username + ":following";
	}
	
	public static String userAll() {
		return "user:all";
	}
	
	public static String nextUserId() {
		return "global:userid";
	}
	
	public static String auth(String auth) {
		return "auth:" + auth + ":username";
	}
	

}

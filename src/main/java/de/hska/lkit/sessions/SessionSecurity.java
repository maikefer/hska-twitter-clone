package de.hska.lkit.sessions;

import org.springframework.core.NamedThreadLocal;

public abstract class SessionSecurity {

	private static final ThreadLocal<SessionData> data = new NamedThreadLocal<SessionData>("twitterclone-id");

	private static class SessionData {
		String username;
		String token;
		
		public SessionData(String un, String t) {
			username = un;
			token = t;
		}
	}
	
	public static void set(String username, String token) {
		data.set(new SessionData(username, token));
	}
	
	public static void clear() {
		data.set(null);
	}

	public static boolean isUserSignedIn(String name) {
		return data.get().username.equals(name);
	}

	public static boolean isSignedIn() {
		return data.get() == null;
	}

	public static String getName() { 
		return data.get().username;
	}
	
	public static String getToken() {		 
		return data.get().token;
	}

}

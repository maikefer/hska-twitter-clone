package de.hska.lkit.redis.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @author essigt
 *
 */
public class Post implements Serializable {

	// UID
	private static final long serialVersionUID = 835044880423989958L;

	private String id;
	private LocalDateTime timestamp;
	private String message;
	private String user;

	public Post() {

	}

	/**
	 * @param id
	 * @param timestamp
	 * @param message
	 */
	public Post(String id, LocalDateTime timestamp, String message, String user) {
		this.id = id;
		this.timestamp = timestamp;
		this.message = message;
		this.user = user;
	}
	
	

	/**
	 * @param message
	 * @param user
	 */
	public Post(String message, String user) {
		this.timestamp = LocalDateTime.now();
		this.message = message;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", timestamp=" + timestamp + ", message=" + message + ", user=" + user + "]";
	}

}

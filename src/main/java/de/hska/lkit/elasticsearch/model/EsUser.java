package de.hska.lkit.elasticsearch.model;

import org.springframework.data.elasticsearch.annotations.Document;

import de.hska.lkit.redis.model.User;

@Document(indexName = "users")
public class EsUser {

	private String id;
	private String username;

	public EsUser() {
	}

	public EsUser(User user) {
		this.id = user.getUsername();
		this.username = user.getUsername();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "EsUser [username=" + username + "]";
	}

}

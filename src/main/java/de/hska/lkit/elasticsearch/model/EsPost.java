package de.hska.lkit.elasticsearch.model;

import org.springframework.data.elasticsearch.annotations.Document;

import de.hska.lkit.redis.model.Post;

@Document(indexName = "posts")
public class EsPost{

	private String id;
	private String message;

	public EsPost() {	
	}
	
	public EsPost(Post post) {
		this.id = post.getId();
		this.message = post.getMessage();
	}

	/**
	 * @param id
	 * @param message
	 */
	public EsPost(String id, String message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		EsPost other = (EsPost) obj;
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
		return true;
	}

	@Override
	public String toString() {
		return "EsPost [id=" + id + ", message=" + (message == null ? "null" : message.replace("\n", "\\n")) + "]";
	}

}

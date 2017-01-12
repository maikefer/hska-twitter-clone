package de.hska.lkit.elasticsearch.repo;



import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import de.hska.lkit.elasticsearch.model.EsPost;


public interface ESPostRepository extends ElasticsearchRepository<EsPost, String>{

	/**
	 * 
	 * @param message
	 * @return
	 */
	List<EsPost> findByMessageLike(String message);
	
}

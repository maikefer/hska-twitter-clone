package de.hska.lkit.elasticsearch.repo;



import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import de.hska.lkit.elasticsearch.model.EsPost;


public interface ESPostRepository extends ElasticsearchRepository<EsPost, String>{

	//@Query("{\"bool\" : {\"must\" : {\"field\" : {\"message\" : {\"query\" : \"*?*\",\"analyze_wildcard\" : true}}}}}")
	List<EsPost> findByMessageLike(String message);
	
}

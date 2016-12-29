package de.hska.lkit.elasticsearch.repo;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import de.hska.lkit.elasticsearch.model.EsUser;


/**
 * 
 * @author essigt
 *
 */
public interface ESUserRepository extends ElasticsearchRepository<EsUser, String>{

	/**
	 * 
	 * @param username
	 * @return
	 */
	//@Query("{\"bool\" : {\"must\" : {\"field\" : {\"username\" : {\"query\" : \"?*\",\"analyze_wildcard\" : true}}}}}")
	//@Query("{\"query\": {   \"match_phrase\": {\"username\": \"?\"}}}")
	List<EsUser> findByUsernameLike(String username);
	
}
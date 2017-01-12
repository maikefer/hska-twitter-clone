package de.hska.lkit.elasticsearch.configuration;

import java.net.InetSocketAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "de/hska/lkit/elasticsearch/repo")
public class ElasticsearchConfiguration {

	@Bean
    public ElasticsearchOperations elasticsearchTemplate() {
		//Old Version using local instance
        //return new ElasticsearchTemplate(NodeBuilder.nodeBuilder().local(true).clusterName("lkit").node().client());
		
		//New Version using TransportClient and shared ES instance
		return new ElasticsearchTemplate(buildClient());
    }
	
	@Bean
	public Client buildClient() {
			String esHost = System.getenv().getOrDefault("ES_HOST", "localhost");
			
	        TransportClient client = TransportClient.builder().build();
	        TransportAddress address = new InetSocketTransportAddress(new InetSocketAddress(esHost, 9300));
	        client.addTransportAddress(address);
	        return client;
	}
	
}

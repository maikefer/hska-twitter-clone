package de.hska.lkit.redis.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.hska.lkit.redis.model.Post;

/**
 *
 * @author essigt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostRepositoryIT {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;


	@Test
	public void postTests() {
		String follower = "test:testUserForPostITfollower";
		Post post = new Post("Mein toller Post", "test:testUserForPostIT");

		//Follow the posting user
		userRepository.startFollowUser(post.getUser(), follower);

		int numPostsBefore = postRepository.findAllPosts().size();

		postRepository.savePost(post);

		int numPostsAfter = postRepository.findAllPosts().size();

		assertEquals("Number of Posts should be incremented by one", 1, numPostsAfter - numPostsBefore);

		Post foundPost = postRepository.findPost(post.getId());
		assertEquals("Posts should be equal", post, foundPost);

		List<Post> postsByUser = postRepository.findPostsByUser(post.getUser());
		assertTrue("Post should be in list of posts by this user", postsByUser.contains(post));


		List<Post> timelineOfUser = postRepository.timelineOfUser(follower);
		assertTrue("Timeline of Follower should contain post of test user", timelineOfUser.contains(post));
	}

}

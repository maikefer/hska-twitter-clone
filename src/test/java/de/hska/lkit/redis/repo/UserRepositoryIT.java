package de.hska.lkit.redis.repo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.hska.lkit.redis.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryIT {

	@Autowired
	private UserRepository userRepository;

	private User user;

	@Before
	public void setup() {
		//JUnit4, no @BeforeAll, setup is executed before each method, changes made to the user are not stored between tests
		user = new User();
		user.setUsername("test:ohIRealyHopeNoOneEverUsesThisUsernameInProduction");
		user.setPassword("xyz");
		user.setEmail("tim@someotheremail.com");
	}

	@Test
	public void basic() {
		assertNotNull(this.userRepository);
	}

	@Test
	public void createUser() {
		assertEquals("Username should be still available", true, this.userRepository.isUsernameAvailable(this.user.getUsername()) );

		int numUsersBefore = this.userRepository.findAllUsers().size();
		this.userRepository.saveUser(this.user);
		int numUsersAfter = this.userRepository.findAllUsers().size();

		assertEquals("Number of users has increased", 1, numUsersAfter - numUsersBefore);

		assertEquals("Username shouldn't be available anymore", false, this.userRepository.isUsernameAvailable(this.user.getUsername()) );
	}

	@Test
	public void findUser() {
		createUser();

		User foundUser = this.userRepository.findUser( user.getUsername() );

		assertEquals("The found user should be equal to the test user", this.user, foundUser);

		deleteUser();
	}

	@Test
	public void deleteUser() {
		int numUsersBefore = this.userRepository.findAllUsers().size();
		this.userRepository.deleteUser(this.user);
		int numUsersAfter = this.userRepository.findAllUsers().size();

		assertEquals("Number of users has decreased", -1, numUsersAfter - numUsersBefore);
	}

	@Test
	public void followUser() {
		String user1 = "test:user1";
		String user2 = "test:user2";

		this.userRepository.startFollowUser(user1, user2);
		assertTrue("User2 is a follower of User1",  this.userRepository.findFollowers(user1).contains(user2));
		assertTrue("User2 is following User1",  this.userRepository.findFollowing(user2).contains(user1));

		this.userRepository.stopFollowUser(user1, user2);
		assertFalse("User2 is not a follower of User1 anymore",  this.userRepository.findFollowers(user1).contains(user2));
		assertFalse("User2 is not following User1 anymore",  this.userRepository.findFollowing(user2).contains(user1));
	}

}

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
	private UserRepository userRepositroy;
	
	private User user;
	
	@Before
	public void setup() {
		//JUnit4, no @BeforeAll, setup is executed before each method, changes made to the user are not stored between tests
		user = new User();
		user.setFirstname("Tim");
		user.setLastname("Essig");
		user.setUsername("test:ohIRealyHopeNoOneEverUsesThisUsernameInProduction");
		user.setPassword("xyz");
	}
	
	@Test
	public void basic() {
		assertNotNull(this.userRepositroy);
	}
	
	@Test
	public void createUser() {
		assertEquals("Username should be still available", true, this.userRepositroy.isUsernameAvailable(this.user.getUsername()) );
		
		int numUsersBefore = this.userRepositroy.findAllUsers().size();		
		this.userRepositroy.saveUser(this.user);		
		int numUsersAfter = this.userRepositroy.findAllUsers().size();
		
		assertEquals("Number of users has increased", 1, numUsersAfter - numUsersBefore);
		
		assertEquals("Username shouldn't be available anymore", false, this.userRepositroy.isUsernameAvailable(this.user.getUsername()) );
	}
	
	@Test
	public void findUser() {
		createUser();
		
		User foundUser = this.userRepositroy.findUser( user.getUsername() );
		
		assertEquals("The found user should be equal to the test user", this.user, foundUser);
		
		deleteUser();
	}
	
	@Test
	public void deleteUser() {
		int numUsersBefore = this.userRepositroy.findAllUsers().size();		
		this.userRepositroy.deleteUser(this.user);		
		int numUsersAfter = this.userRepositroy.findAllUsers().size();
		
		assertEquals("Number of users has decreased", -1, numUsersAfter - numUsersBefore);
	}
	
	@Test
	public void followUser() {
		String user1 = "test:user1";
		String user2 = "test:user2";
		
		this.userRepositroy.startFollowUser(user1, user2);
		assertTrue("User2 is a follower of User1",  this.userRepositroy.findFollowers(user1).contains(user2));
		assertTrue("User2 is following User1",  this.userRepositroy.findFollowing(user2).contains(user1));
		
		this.userRepositroy.stopFollowUser(user1, user2);
		assertFalse("User2 is not a follower of User1 anymore",  this.userRepositroy.findFollowers(user1).contains(user2));
		assertFalse("User2 is not following User1 anymore",  this.userRepositroy.findFollowing(user2).contains(user1));
	}

}

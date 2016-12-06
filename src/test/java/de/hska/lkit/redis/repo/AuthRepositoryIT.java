package de.hska.lkit.redis.repo;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.hska.lkit.redis.model.User;

/**
 *
 * @author essigt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthRepositoryIT {

	@Autowired
	private AuthRepository authRepositroy;

	@Autowired
	private UserRepository userRepository;


	@Test
	public void auth() {
		User user = new User();
		user.setPassword("xyz");
		user.setUsername("test:authTestUser");
		user.setEmail("tim@test.com");

		userRepository.saveUser(user);

		assertNull("Invalid Auth-Token should be recogniced as invalid", authRepositroy.isAuthValid("xyzInfalidAuth") );

		assertFalse("Wrong authentification should not succeed", authRepositroy.auth(user.getUsername(), user.getPassword() + "WRONG!!!PW"));

		boolean authSuccessfull = authRepositroy.auth(user.getUsername(), user.getPassword());
		assertTrue("Authentification should succeed", authSuccessfull);

		String authToken = authRepositroy.addAuth(user.getUsername(), 3, TimeUnit.SECONDS);

		assertEquals("Auth-Token should still be falid", user.getUsername(), authRepositroy.isAuthValid(authToken));

		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Sleep for 4 Seconds has been interrupted");
		}

		assertNull("Auth-Token shouldn't be valid anymore", authRepositroy.isAuthValid(authToken));

		authToken = authRepositroy.addAuth(user.getUsername(), 10, TimeUnit.SECONDS);
		assertEquals("Auth-Token should still be falid", user.getUsername(), authRepositroy.isAuthValid(authToken));

		authRepositroy.deleteAuth(user.getUsername());

		assertNull("Auth-Token shouldn't be valid anymore", authRepositroy.isAuthValid(authToken));

	}

}

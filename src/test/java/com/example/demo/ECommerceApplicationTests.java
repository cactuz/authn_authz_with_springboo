package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=ECommerceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ECommerceApplicationTests {
	@LocalServerPort
	private int port;
	private static HttpHeaders headers;
	private static String CREATE_USER_PATH = "/api/user/create";
	private static String LOGIN_PATH = "/login";
	private static String PASSWORD = TestUtils.getTestUser().getPassword();

	@BeforeClass
	public static void oneTimeSetUp() {
		headers = new HttpHeaders();
		//headers.add("Accept", "application/json");
		headers.setContentType(MediaType.APPLICATION_JSON);
		//headers.set("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
	}

	@Before
	public void init() {
	}

	@Test
	public void verifyCreateUserRequestAndLoginTest() {
		//A new user ID is created for each test. It is not possible to create one via @BeforeClass.
		//That test method will require static variables. Unfortunately, the port may not be generated statically.
		CreateUserRequest createUserRequest = new CreateUserRequest();
		String randomUserName = UUID.randomUUID().toString();
		createUserRequest.setUsername(randomUserName);
		createUserRequest.setPassword(PASSWORD);
		createUserRequest.setConfirmPassword(PASSWORD);

		HttpEntity<CreateUserRequest> createUserRequestHttpEntity= new HttpEntity<>(createUserRequest, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<User> userResponseEntity;
		userResponseEntity = restTemplate.postForEntity(getURL(CREATE_USER_PATH), createUserRequestHttpEntity, User.class);

		System.out.println("rudy jwt: " + getJWTForNewUser(randomUserName));

		Assert.assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
		Assert.assertEquals(randomUserName, userResponseEntity.getBody().getUsername());
	}

	@Test
	public void Test() {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		String randomUserName = UUID.randomUUID().toString();
		createUserRequest.setUsername(randomUserName);
		createUserRequest.setPassword(TestUtils.getTestUser().getPassword());
		createUserRequest.setConfirmPassword(TestUtils.getTestUser().getPassword());
		HttpEntity<CreateUserRequest> createUserRequestHttpEntity= new HttpEntity<>(createUserRequest, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<User> userResponseEntity;
		userResponseEntity = restTemplate.postForEntity(getURL(CREATE_USER_PATH), createUserRequestHttpEntity, User.class);

		System.out.println("rudy jwt: " + getJWTForNewUser(randomUserName));

		Assert.assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
		Assert.assertEquals(randomUserName, userResponseEntity.getBody().getUsername());
	}


	private String getURL(String path) {
		String url = "http://localhost:" + port + path;
		return url;
	}

	private String getJWTForNewUser(String useName) {
		String userName = UUID.randomUUID().toString();

		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(userName);
		createUserRequest.setPassword(PASSWORD);
		createUserRequest.setConfirmPassword(PASSWORD);

		//creates a user
		HttpEntity<CreateUserRequest> createUserRequestHttpEntity= new HttpEntity<>(createUserRequest, headers);
		RestTemplate restTemplate = new RestTemplate();
		User user = new User();
		user = restTemplate.postForEntity(getURL(CREATE_USER_PATH), createUserRequestHttpEntity, User.class).getBody();

		//user login and generate JWT

		HttpEntity<User> useLoginRequestHttpEntity= new HttpEntity<>(user, headers);
		restTemplate.postForEntity(getURL(LOGIN_PATH), useLoginRequestHttpEntity, User.class);
//				.getHeaders()
//				.toSingleValueMap()
//				.get("Authorization");
		return "hello";
	}
}

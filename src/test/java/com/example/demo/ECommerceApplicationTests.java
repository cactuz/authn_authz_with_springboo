package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import java.util.*;

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
	private static String ADD_TO_CART_PATH = "/api/cart/addToCart";
	private static String SUBMIT_ORDER_PATH = "/api/order/submit/";
	private static String PASSWORD = "password";
	private static long TEST_ITEM = 1L;

	@BeforeClass
	public static void oneTimeSetUp() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Before
	public void init() {
	}

	@Test
	public void verifyCreateUserRequestAndLoginTest() {
		//A new user ID is created for each test. It is not possible to create one via @BeforeClass.
		//That test method will require static variables. Unfortunately, the port may not be generated statically.
		String userName = UUID.randomUUID().toString();

		List<ResponseEntity> response = getJWTForNewUser(userName);

		ResponseEntity<User> createUserResponse = response.get(0);
		ResponseEntity<Void> loginResponse = response.get(1);
		String jwt = loginResponse.getHeaders().toSingleValueMap().get("Authorization");
		headers.add("Authorization", jwt);
		Assert.assertEquals("Create User Http Response is 200", HttpStatus.OK, createUserResponse.getStatusCode());
		Assert.assertEquals("Create User Response Name is same as input", userName, createUserResponse.getBody().getUsername());
		Assert.assertEquals("Login New User response is 200", HttpStatus.OK, loginResponse.getStatusCode());
		Assert.assertNotNull("Login header contains JWT", jwt);

		ResponseEntity<Cart> addToCartResponse = sendAddToCartRequest(userName, headers);
		Assert.assertEquals("Add to cart request is successful", HttpStatus.OK, addToCartResponse.getStatusCode());
		Assert.assertEquals("Item number in cart matches submission", TEST_ITEM,
				Long.parseLong(addToCartResponse.getBody().getItems().get(0).getId().toString()));

		ResponseEntity<Cart> submitOrderResponse = submitOrderReqquest(userName, headers);
		Assert.assertEquals("Add to cart request is successful", HttpStatus.OK, submitOrderResponse.getStatusCode());
	}

	private String getURL(String path) {
		String url = "http://localhost:" + port + path;
		return url;
	}

	/**
	 * Creates a new user and login the new user for authorization get the JWT (Json Web Token).
	 * @param userName
	 * @return
	 */
	private List<ResponseEntity> getJWTForNewUser(String userName) {
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(userName);
		createUserRequest.setPassword(PASSWORD);
		createUserRequest.setConfirmPassword(PASSWORD);

		HttpEntity<CreateUserRequest> createUserRequestHttpEntity= new HttpEntity<>(createUserRequest, headers);
		RestTemplate createUserRestTemplate = new RestTemplate();
		ResponseEntity<User> createUserResponse = createUserRestTemplate.postForEntity(getURL(CREATE_USER_PATH), createUserRequestHttpEntity, User.class);

		Map<String, String> body = new HashMap<>();
		body.put("username", userName);
		body.put("password", PASSWORD);

		RestTemplate loginTemplate = new RestTemplate();
		//must not use User entity for the body; it will create a recursive loop in UserDetailsServiceImpl, loadUserByUserName
		ResponseEntity<Void> loginResponse = loginTemplate.postForEntity(getURL(LOGIN_PATH), body, null);

		List<ResponseEntity> list = new ArrayList<>();
		list.add(createUserResponse);
		list.add(loginResponse);

		return list;
	}

	/**
	 * Adds a test item to a cart for an authenticated user.
	 * @param userName
	 * @param headers
	 * @return
	 */
	private ResponseEntity<Cart> sendAddToCartRequest(String userName, HttpHeaders headers) {

		ModifyCartRequest cart = new ModifyCartRequest();
		cart.setUsername(userName);
		cart.setItemId(TEST_ITEM);
		cart.setQuantity(2);
		HttpEntity<ModifyCartRequest> cartRequestHttpEntity = new HttpEntity<>(cart, headers);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForEntity(getURL(ADD_TO_CART_PATH), cartRequestHttpEntity, Cart.class);
	}

	/**
	 * Submits cart of an authenticated user.
	 * @param userName
	 * @param headers
	 * @return
	 */
	private ResponseEntity<Cart> submitOrderReqquest(String userName, HttpHeaders headers) {
		HttpEntity<Void> orderHttpEntity = new HttpEntity<>(null, headers);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForEntity(getURL(SUBMIT_ORDER_PATH+userName), orderHttpEntity, Cart.class);
	}
}

package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;


public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private CartRepository cartRepository = Mockito.mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    private static User user = TestUtils.getTestUser();
    private static final String INVALID_TEST_PASSWORD = "short";

    @Before
    public void init() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void findUserByIdTest() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);
        Assert.assertEquals(1, response.getBody().getId());
        Assert.assertEquals(user.getUsername(), response.getBody().getUsername());
        Assert.assertEquals(user.getPassword(), response.getBody().getPassword());
    }

    @Test
    public void findUserNameSuccessTest() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1, response.getBody().getId());
        Assert.assertEquals(user.getUsername(), response.getBody().getUsername());
        Assert.assertEquals(user.getPassword(), response.getBody().getPassword());
    }

    @Test
    public void findUserNameNotFoundTest() throws Exception {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void createUserValidTest() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword(user.getPassword());
        userRequest.setConfirmPassword(user.getPassword());

        String hashedPassword = "mockedHashedValue";
        Mockito.when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn(hashedPassword);

        ResponseEntity<User> response = userController.createUser(userRequest);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(userRequest.getUsername(), response.getBody().getUsername());
        Assert.assertEquals(hashedPassword, response.getBody().getPassword());
    }

    @Test
    public void createUserInvalidPasswordTest() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword(INVALID_TEST_PASSWORD);
        userRequest.setConfirmPassword(INVALID_TEST_PASSWORD);

        ResponseEntity<User> response = userController.createUser(userRequest);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void createUserNonMatchingConfirmPasswordTest() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword(user.getPassword());
        userRequest.setConfirmPassword(INVALID_TEST_PASSWORD);

        ResponseEntity<User> response = userController.createUser(userRequest);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }
}

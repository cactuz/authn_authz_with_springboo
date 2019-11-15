package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private User userWithOneItemInCart = TestUtils.getTestUser();
    private List<UserOrder> userOrderList;

    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);

        userWithOneItemInCart.setCart(TestUtils.getTestCartWithOneShirt());
    }

    @Test
    public void submitOrderValidTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(userWithOneItemInCart);

        ResponseEntity<UserOrder> response = orderController.submit(TestUtils.getTestUser().getUsername());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(userWithOneItemInCart.getCart().getItems(), response.getBody().getItems());
    }

    @Test
    public void submitOrderUserNotFoundTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(TestUtils.getTestUser().getUsername());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void submitGetOrderForUserValidTest() {
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(TestUtils.getTestUser());
        userOrder.setItems(TestUtils.getTestItems());
        userOrder.setTotal(TestUtils.getTestItemShirt().getPrice().add(TestUtils.getTestItemJeans().getPrice()));
        userOrder.setId(1L);
        userOrderList = new ArrayList<>();
        userOrderList.add(userOrder);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(userWithOneItemInCart);
        Mockito.when(orderRepository.findByUser(Mockito.any())).thenReturn(userOrderList);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.getTestUser().getUsername());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(userOrderList, response.getBody());
        Assert.assertEquals(TestUtils.getTestUser().getUsername(), response.getBody().get(0).getUser().getUsername());
        Assert.assertEquals(userOrder.getTotal(), response.getBody().get(0).getTotal());
    }

    @Test
    public void submitGetOrderForUserAndUserIsNotFoundTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.getTestUser().getUsername());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }
}
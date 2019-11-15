package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private CartRepository cartRepository = Mockito.mock(CartRepository.class);;
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private ModifyCartRequest cartRequest;
    private User user = TestUtils.getTestUser();
    private Item testItem = TestUtils.getTestItemJeans();
    private static final int NUMBER_OF_ITEMS_1 = 1;
    private static final int NUMBER_OF_ITEMS_2 = 2;

    @Before
    public void init() {
        cartController = new CartController();

        cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(testItem.getId());
        cartRequest.setQuantity(NUMBER_OF_ITEMS_1);

        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
    }

    @Test
    public void addAnItemToAnEmptyCartValidTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testItem));

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(testItem.getPrice(), response.getBody().getTotal());
    }

    @Test
    public void addToCartUserNotFoundTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void addToCartItemNotFoundTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void addAnAdditionalItemToACartValidTest() {
        user.setCart(TestUtils.getTestCartWithOneShirt());

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testItem));

        cartRequest.setQuantity(NUMBER_OF_ITEMS_2);
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestUtils.getTestItemShirt().getPrice().add(testItem.getPrice().multiply(new BigDecimal(NUMBER_OF_ITEMS_2))),
                 response.getBody().getTotal());
    }

    @Test
    public void removeFromCartTest() {
        user.setCart(TestUtils.getTestCartWithOneShirt());

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestUtils.getTestItemShirt()));

        cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(TestUtils.getTestItemShirt().getId());
        cartRequest.setQuantity(NUMBER_OF_ITEMS_1);

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(0, response.getBody().getItems().size());
    }

    @Test
    public void removeFromCartUserNotFoundTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void removeFromCartItemNotFoundTest() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(null, response.getBody());
    }
}

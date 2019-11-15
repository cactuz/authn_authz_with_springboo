package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    private static final String USERNAME = "testUserName";
    private static final String VALID_TEST_PASSWORD = "testPassword";

    /**
     * This is used to inject the mock in unit test
     * @param target  The Object where the mock will be inserted
     * @param fieldName The field name in the object that the Mock will replace
     * @param toInject The mocked object.
     */

    public static void injectObject(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if(!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, toInject);

            if(wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User getTestUser() {
        User user = new User();
        user.setId(1);
        user.setUsername(USERNAME);
        user.setPassword(VALID_TEST_PASSWORD);

        user.setCart(new Cart());
        return user;
    }

    public static List<Item> getTestItems() {
        List<Item> itemsList = new ArrayList<>();

        itemsList.add(getTestItemShirt());
        itemsList.add(getTestItemJeans());
        return itemsList;
    }

    public static Cart getTestCartWithOneShirt() {
        Cart cart = new Cart();

        cart.setId(1L);
        cart.addItem(getTestItemShirt());

        return cart;
    }

    public static Item getTestItemShirt() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Shirt");
        item.setPrice(new BigDecimal(129.99));

        return item;
    }

    public static Item getTestItemJeans() {
        Item item = new Item();
        item = new Item();
        item.setId(2L);
        item.setName("Skinny Jeans");
        item.setPrice(new BigDecimal(245.69));

        return item;
    }
}

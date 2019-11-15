package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    @Before
    public void init() {
        itemController = new ItemController();

        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsTest() {
        Mockito.when(itemRepository.findAll()).thenReturn(TestUtils.getTestItems());

        ResponseEntity<List<Item>> response = itemController.getItems();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestUtils.getTestItems(), response.getBody());
    }

    @Test
    public void getItemsByIdTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestUtils.getTestItemShirt()));

        ResponseEntity<Item> response = itemController.getItemById(1L);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestUtils.getTestItemShirt(), response.getBody());
    }

    @Test
    public void getItemsByNameTest() {
        List<Item> oneItemInList = new ArrayList<>();
        oneItemInList.add(TestUtils.getTestItemJeans());

        Mockito.when(itemRepository.findByName(Mockito.anyString())).thenReturn(oneItemInList);


        ResponseEntity<List<Item>> response = itemController.getItemsByName(TestUtils.getTestItemJeans().getName());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(oneItemInList.get(0).hashCode(), response.getBody().get(0).hashCode());
    }
}

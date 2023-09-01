package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemRepository itemRepository;
    private List<Item> listItem;
    private List<Item> listItemByName;
    public static Item initItem(long id, String name, double price, String desc){
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(price));
        item.setDescription(desc);
        return item;
    }
    @Before
    public void setUp(){
        Item item1 = initItem(1L,"Table",1000,"This is a table");
        Item item2 = initItem(2L,"Table",2000,"This is a table");
        Item item3 = initItem(3L,"Book",3000,"This is a book");
        listItem = Arrays.asList(new Item[]{item1, item2, item3});
        listItemByName = Arrays.asList(new Item[]{item1, item2});
    }

    @Test
    public void testGetAllItem() throws Exception {
        when(itemRepository.findAll()).thenReturn(listItem);

        ResponseEntity<List<Item>> response = itemController.getItems();

        List<Item> items = response.getBody();
        assertEquals(200,response.getStatusCodeValue());
        assertEquals(3,response.getBody().size());
        assertEquals(listItem,items);
    }

    @Test
    public void testGetItemById() throws Exception {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(listItem.get(0)));

        ResponseEntity<Item> response = itemController.getItemById(1L);
        Item item = response.getBody();

        assertEquals(200,response.getStatusCodeValue());
        assertEquals("Table",item.getName());
        assertEquals("This is a table",item.getDescription());
        assertEquals(BigDecimal.valueOf(1000.0),item.getPrice());
    }

    @Test
    public void testGetItemNotFoundById() throws Exception {
        ResponseEntity<Item> response = itemController.getItemById(4L);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void testGetItemByName() throws Exception {
        when(itemRepository.findByName("Table")).thenReturn(listItemByName);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Table");

        List<Item> items = response.getBody();
        assertEquals(200,response.getStatusCodeValue());
        assertEquals(2,response.getBody().size());
        assertEquals(listItemByName,items);
    }

    @Test
    public void testGetItemNotFoundByName() throws Exception {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Name not exists");
        assertEquals(404,response.getStatusCodeValue());
    }
}

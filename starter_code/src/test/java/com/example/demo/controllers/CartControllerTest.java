package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartController cartController;

    public static User initUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
    public static Item initItem(long id, String name, double price, String desc){
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(price));
        item.setDescription(desc);
        return item;
    }
    public static ModifyCartRequest initModifyCartRequest (String username, long itemId, int quantity){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(quantity);
        return modifyCartRequest;
    }
    public static Cart initCart(Long id, User user, List<Item> item){
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUser(user);
        cart.setItems(item);
        return cart;
    }
    @Test
    public void testAddToCart() {
        Long userId = 1L;
        String username = "CangNT2";
        Long itemId = 1L;
        String itemName = "Table";
        String des = "This is a table";
        int quantity = 10;
        double price = 1000;

        User user = initUser(userId, username);
        Item item = initItem(itemId, itemName, price, des);
        ModifyCartRequest modifyCartRequest = initModifyCartRequest(username, itemId, quantity);

        List<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = initCart(1L, user, items);

        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cart, response.getBody());
    }

    @Test
    public void testAddToCartInvalidUser() {
        String username = "testuser";
        Long itemId = 1L;
        int quantity = 10;

        ModifyCartRequest request = initModifyCartRequest(username, itemId, quantity);
        when(userRepository.findByUsername(username)).thenReturn(null);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCartInvalidItem() {
        String username = "CangNT2";
        Long itemId = 1L;
        int quantity = 10;

        User user = initUser(1L, username);
        ModifyCartRequest request = initModifyCartRequest(username, itemId, quantity);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveCart() {

        Long userId = 1L;
        String username = "CangNT2";
        Long itemId = 1L;
        String itemName = "Table";
        String des = "This is a table";
        int quantity = 10;
        double price = 1000;

        User user = initUser(userId, username);
        Item item = initItem(itemId, itemName, 1000, des);
        ModifyCartRequest modifyCartRequest = initModifyCartRequest(username, itemId, quantity);

        List<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = initCart(1L, user, items);

        user.setCart(cart);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void testRemoveCartInvalidUsername() {
        long userId = 1L;
        String username = "CangNT2";
        User user = initUser(userId, username);
        ModifyCartRequest request = new ModifyCartRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(null);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveInvalidItemId() {
        long userId = 1L;
        String username = "CangNT2";
        User user = initUser(userId, username);

        ModifyCartRequest request = new ModifyCartRequest();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
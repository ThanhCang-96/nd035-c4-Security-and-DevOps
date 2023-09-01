package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    private static User user;
    private static Cart cart;
    private static Item item;
    private static UserOrder order;

    public static User initUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
    public static Cart initCart(Long id, User user, List<Item> item){
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUser(user);
        cart.setItems(item);
        return cart;
    }
    public static Item initItem(long id, String name, double price, String desc){
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(price));
        item.setDescription(desc);
        return item;
    }
    public static UserOrder initOrder (long id, List<Item> items, User user, double total){
        UserOrder userOrder = new UserOrder();
        userOrder.setId(id);
        userOrder.setItems(items);
        userOrder.setUser(user);
        userOrder.setTotal(BigDecimal.valueOf(total));
        return  userOrder;
    }
    @Before
    public void setUp(){
        user = initUser(1L, "CangNT2");
        item = initItem(1L, "Table", 1000, "This is a table");
        List<Item> items = new ArrayList<>();
        items.add(item);
        cart = initCart(1L, user, items);
        user.setCart(cart);
        order = initOrder(1L, items, user, 1000);
    }
    @Test
    public void testCreateOrderByUser() throws Exception {
        when(userRepository.findByUsername("CangNT2")).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit("CangNT2");

        UserOrder userOrder = response.getBody();
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("CangNT2",userOrder.getUser().getUsername());
    }
    @Test
    public void testCreateOrderByInvalidUser() throws Exception {
        ResponseEntity<UserOrder> response = orderController.submit("invalidUser");
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testGetOrderByUser() throws Exception {
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);
        when(userRepository.findByUsername("CangNT2")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("CangNT2");

        List<UserOrder> userOrders = response.getBody();
        assertEquals(1,userOrders.size());
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("CangNT2",userOrders.get(0).getUser().getUsername());
        assertEquals(BigDecimal.valueOf(1000.0),userOrders.get(0).getTotal());
        assertEquals("Table",userOrders.get(0).getItems().get(0).getName());
    }

    @Test
    public void testGetOrderByInvalidUser() throws Exception {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("invalidUser");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private CreateUserRequest createUserRequest;
    private User user;
    @Before
    public void setUp() throws Exception {
        createUserRequest = new CreateUserRequest();
        user = new User();
    }
    @Test
    public void testCreateUser() throws Exception {
        when(bCryptPasswordEncoder.encode("Test123456")).thenReturn("encodedPassword");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("cangnt2");
        request.setPassword("Test123456");
        request.setConfirmPassword("Test123456");
        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(200,response.getStatusCodeValue());
        User user = response.getBody();
        assertEquals(0,user.getId());
        assertEquals("cangnt2",user.getUsername());
        assertEquals("encodedPassword",user.getPassword());
    }

    @Test
    public void testCreateUserInvalidPassword() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        // length password < 7
        request.setPassword("123456");
        request.setConfirmPassword("123456");

        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateUserInvalidConfirmPassword() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("12345678");
        request.setConfirmPassword("11111111");

        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        long id = 1L;
        user.setId(id);
        user.setUsername("username");
        user.setPassword("password");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(id);

        User user1 = response.getBody();
        assertEquals(200,response.getStatusCodeValue());
        assertEquals("username",user1.getUsername());
        assertEquals("password",user1.getPassword());
    }

    @Test
    public void testFindUserByInvalidId() {
        ResponseEntity<User> response = userController.findById(1L);
        assertEquals(404,response.getStatusCodeValue());
    }
    @Test
    public void testFindUserByUsername() {
        User user = new User();
        String username = "username";
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("username");

        User user1 = response.getBody();
        assertEquals(200,response.getStatusCodeValue());
        assertEquals("username",user1.getUsername());
        assertEquals("password",user1.getPassword());
    }
    @Test
    public void testFindUserByInvalidUsername() {
        ResponseEntity<User> response = userController.findByUserName("username");
        assertEquals(404,response.getStatusCodeValue());
    }
}
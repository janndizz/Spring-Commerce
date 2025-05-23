package vn.edu.tdtu.javatech.springcommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.tdtu.javatech.springcommerce.model.User;
import vn.edu.tdtu.javatech.springcommerce.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setRole(User.Role.CLIENT);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setRole(User.Role.ADMIN);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));

        // Act
        User foundUser = userService.findByUsername("user1");

        // Assert
        assertNotNull(foundUser);
        assertEquals("user1", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnNull() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findByUsername("unknown");

        // Assert
        assertNull(foundUser);
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void saveUser_WithNewUser_ShouldSaveUser() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(user1);

        // Act
        userService.saveUser(newUser);

        // Assert
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(newUser);
        assertEquals("encodedPassword", newUser.getPassword());
    }

    @Test
    void saveUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("user1");
        existingUser.setEmail("new@example.com");

        when(userRepository.existsByUsername("user1")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.saveUser(existingUser);
        });

        assertEquals("Username đã tồn tại", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("user1");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("newuser");
        existingUser.setEmail("user1@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.saveUser(existingUser);
        });

        assertEquals("Email đã tồn tại", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("user1@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act
        User foundUser = userService.getUserById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(99L);
        });

        assertEquals("Không tìm thấy người dùng với ID = 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void updateUser_ShouldUpdateExistingUser() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPhonenumber("123456789");
        updatedUser.setRole(User.Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        userService.updateUser(1L, updatedUser);

        // Assert
        assertEquals("updatedUser", user1.getUsername());
        assertEquals("updated@example.com", user1.getEmail());
        assertEquals("123456789", user1.getPhonenumber());
        assertEquals("ADMIN", user1.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void save_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(user1);

        // Act
        User savedUser = userService.save(newUser);

        // Assert
        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals(user1, savedUser);
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void existsByUsername_ShouldReturnTrueWhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("user1")).thenReturn(true);

        // Act
        boolean exists = userService.existsByUsername("user1");

        // Assert
        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername("user1");
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenEmailNotExists() {
        // Arrange
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        // Act
        boolean exists = userService.existsByEmail("unknown@example.com");

        // Assert
        assertFalse(exists);
        verify(userRepository, times(1)).existsByEmail("unknown@example.com");
    }
}
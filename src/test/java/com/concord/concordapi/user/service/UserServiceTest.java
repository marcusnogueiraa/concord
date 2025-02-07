package com.concord.concordapi.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.request.UserPatchImage;
import com.concord.concordapi.user.dto.request.UserPatchName;
import com.concord.concordapi.user.dto.request.UserPatchUsername;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FileStorageService fileStorageService;

    static Random random;
    
    @BeforeAll
    static void setup(){
        random = new Random();
    }

    @Test
    void testGetById_Success() {
        User user = new User();
        user.setId(random.nextLong());
        user.setUsername("testUser");

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
    }

    @Test
    void testGetById_NotFound() {
        Long userIdNotFound = random.nextLong();
        Mockito.when(userRepository.findById(userIdNotFound)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getById(userIdNotFound));

        assertEquals("User "+userIdNotFound+" not found.", exception.getMessage());
    }

    @Test
    void testGetByUsername_Success() {
        User user = new User();
        user.setId(random.nextLong());
        user.setUsername("testUser");

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserDto result = userService.getByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.username());
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername("testUser");
    }

    @Test
    void testGetByUsername_NotFound() {
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getByUsername("testUser"));

        assertEquals("User testUser not found.", exception.getMessage());
    }

    @Test
    void testUpdateUsername_Success() {
        User user = new User();
        user.setUsername("oldUsername");

        UserPatchUsername patch = new UserPatchUsername("newUsername");

        Mockito.when(userRepository.findByUsername("oldUsername")).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());

        UserDto result = userService.updateUsername(patch, "oldUsername");

        assertNotNull(result);
        assertEquals("newUsername", result.username());
        Mockito.verify(authService, Mockito.times(1)).isUserTheAuthenticated(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void testUpdateUsername_UsernameAlreadyInUse() {
        User user = new User();
        user.setUsername("oldUsername");

        UserPatchUsername patch = new UserPatchUsername("existingUsername");

        Mockito.when(userRepository.findByUsername("oldUsername")).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByUsername("existingUsername")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUsername(patch, "oldUsername"));

        assertEquals("This username is already in use", exception.getMessage());
    }

    @Test
    void testUpdateName_Success() {
        User user = new User();
        user.setUsername("testUser");

        UserPatchName patch = new UserPatchName("New Name");

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserDto result = userService.updateName(patch, "testUser");

        assertNotNull(result);
        assertEquals("New Name", result.name());
        Mockito.verify(authService, Mockito.times(1)).isUserTheAuthenticated(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void testUpdateImage_Success() {
        User user = new User();
        user.setUsername("testUser");
        user.setImagePath("old/image/path");

        UserPatchImage patch = new UserPatchImage("newImageTempPath");

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        Mockito.when(fileStorageService.fileExists("old/image/path")).thenReturn(true);

        UserDto result = userService.updateImage(patch, "testUser");

        assertNotNull(result);
        assertEquals("user_image/newImageTempPath", result.imagePath());
        Mockito.verify(authService, Mockito.times(1)).isUserTheAuthenticated(user);
        Mockito.verify(fileStorageService, Mockito.times(1)).persistImage(Mockito.any(), Mockito.eq("newImageTempPath"));
        Mockito.verify(fileStorageService, Mockito.times(1)).deleteFile("old/image/path");
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }
}

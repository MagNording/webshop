package se.nording.webshop;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.nording.webshop.entity.User;
import se.nording.webshop.enums.Role;
import se.nording.webshop.repository.UserRepo;
import se.nording.webshop.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticateTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach // Skapa en användare innan varje test
    public void setUp() {
        user = new User();
        user.setPassword("correctPassword");
        user.setRole(Role.USER);
        when(userRepo.findByUsername("user")).thenReturn(Optional.of(user));
    }

    @Test // Testar att en användare kan autentiseras med rätt lösenord
    public void testAuthUser_WithCorrectPassword() {
        assertTrue(userService.authUser("user", "correctPassword"));
    }

    @Test // Testar att en användare inte kan autentiseras med fel lösenord
    public void testAuthUser_WithIncorrectPassword() {
        assertFalse(userService.authUser("user", "wrongPassword"));
    }

    @Test // Testar att en användare inte kan autentiseras om användaren inte finns
    public void testAuthUser_UserNotFound() {
        when(userRepo.findByUsername("user")).thenReturn(Optional.empty());
        assertFalse(userService.authUser("user", "anyPassword"));
    }

    @Test // Testar att en användare inte kan autentiseras om lösenordet är null
    public void testAuthenticate_UserSuccessfullyAuthenticated() {
        Optional<Role> result = userService.authenticate("user", "correctPassword", session);
        assertTrue(result.isPresent());
        assertEquals(Role.USER, result.get());
        verify(session).setAttribute("loggedInUser", user);
    }

    @Test // Testar att autentiseringen misslyckas om användaren inte finns
    public void testAuthenticate_FailedAuthentication() {
        when(userRepo.findByUsername("user")).thenReturn(Optional.empty());
        Optional<Role> result = userService.authenticate("user", "anyPassword", session);
        assertFalse(result.isPresent());
        verify(session, never()).setAttribute(anyString(), any());
    }
}

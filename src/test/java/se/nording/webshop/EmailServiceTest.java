package se.nording.webshop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import se.nording.webshop.services.EmailService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

// Använd MockitoExtension för att skapa mock-objekt
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test // Testar att ett enkelt meddelande skickas korrekt
    public void testSendSimpleMessage() {
        String to = "user@example.com";
        String subject = "Test Subject";
        String text = "This is the body of the email";

        emailService.sendSimpleMessage(to, subject, text);

        verify(mailSender).send(argThat((ArgumentMatcher<SimpleMailMessage>)
            message -> Objects.requireNonNull(message.getTo())[0].equals(to) &&
            Objects.equals(message.getSubject(), subject) &&
            Objects.equals(message.getText(), text) &&
            Objects.equals(message.getFrom(), "magnus_nording@msn.com"))); // obs anpassa!
    }

    @Test // Testar att registreringsmeddelandet skapas korrekt
    public void testRegMessage() {
        String username = "newUser";
        String expectedMessage = "Thank you for registering!\nHi " + username + ",\nYour registration was successful. Welcome to our webshop!";
        assertEquals(expectedMessage, emailService.regMessage(username));
    }

    @Test // Testar att ordermeddelandet skapas korrekt
    public void testOrderMessage() {
        String username = "newUser";
        String expectedMessage = "Thank you for your order!\nHi " + username + ",\nYour order has been successfully received and is now being processed.";
        assertEquals(expectedMessage, emailService.orderMessage(username));
    }

    @Test
    public void testRegMessageWithEmptyUsername() {
        String username = "";
        String expectedMessage = "Thank you for registering!\nHi ,\nYour registration was successful. Welcome to our webshop!";
        assertEquals(expectedMessage, emailService.regMessage(username));
    }
}

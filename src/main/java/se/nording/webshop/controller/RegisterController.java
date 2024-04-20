package se.nording.webshop.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.nording.webshop.entity.User;
import se.nording.webshop.exceptions.UserAlreadyExistsException;
//import se.tronhage.webshop.services.EmailService;
import se.nording.webshop.services.UserService;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterUserForm(Model m){
        m.addAttribute("customer", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserForm(Model m, @Valid @ModelAttribute("customer") User user,
                                   BindingResult result) {
                                   if (result.hasErrors()) {
                                       return "register";
                                   }
        try {
            userService.registerNewUser(user.getFirstName(), user.getLastName(), user.getEmail(),
                    user.getAddress(), user.getUsername(), user.getPassword());

            // Om användaren registreras korrekt, skicka användaren till inloggningssidan
            m.addAttribute("message", "Registration successful! Please log in.");

        } catch (UserAlreadyExistsException e) {
            m.addAttribute("customer", new User()); // Återställer användarobjektet
            m.addAttribute("errorMessage", "Username already in use.");
            return "register";
        } catch (Exception e) {
            m.addAttribute("customer", new User()); // Återställer användarobjektet
            m.addAttribute("errorMessage", "Error during registration.");
            return "register";
        }
        return "redirect:/login";
    }
}

package se.nording.webshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.nording.webshop.enums.Role;
import se.nording.webshop.repository.UserRepo;
import se.nording.webshop.services.UserService;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;
    private final UserRepo userRepo;

    public LoginController(UserService userService, UserRepo userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String redirectToLogin(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String displayLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestParam String username,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        Optional<Role> role = userService.authenticate(username, password, session);

        if (role.isPresent()) {
            if (role.get() == Role.ADMIN) {
                return "redirect:/admin";
            }
            return "redirect:/webshop";
        } else {
            redirectAttributes.addFlashAttribute("loginErrorMessage",
                    "Invalid username or password");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout() {
        return "login";
    }
}
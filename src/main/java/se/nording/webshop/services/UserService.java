package se.nording.webshop.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import se.nording.webshop.entity.User;
import se.nording.webshop.repository.UserRepo;
import se.nording.webshop.enums.Role;
import se.nording.webshop.exceptions.UserAlreadyExistsException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final EmailService emailService;

    public UserService(UserRepo userRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    public void registerNewUser(String firstName, String lastName, String email, String address,
                                String username, String password) {
        if (userRepo.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already in use.");
        }
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setAddress(address);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(Role.USER);
        userRepo.save(newUser);

        registerEmail(newUser);
    }

    // Skicka bekräftelsemail
    public void registerEmail(User newUser) {
        emailService.sendSimpleMessage(newUser.getEmail(), "Registration Confirmation",
                emailService.regMessage(newUser.getUsername()));
    }

    public Optional<User> findById(Long id) {
        System.out.println("findbyid i service"); // ?
        return userRepo.findById(id);
    }

    public void updateUser(User updatedUser) {
        Long userId = updatedUser.getId();
        System.out.println(updatedUser.toString());
        if (userId != null) {
            Optional<User> optionalUser = findById(userId);
            System.out.println(userId);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();

                // Update the existing user's fields with the form data
                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setAddress(updatedUser.getAddress());
                existingUser.setUsername(updatedUser.getUsername());
                existingUser.setPassword(updatedUser.getPassword());
                existingUser.setRole(updatedUser.getRole());

                userRepo.save(existingUser);
            }
        }
    }

        public boolean authUser (String username, String password){
            Optional<User> user = userRepo.findByUsername(username);
            if (user.isPresent()) {
                String dbPw = user.get().getPassword();
                return password.equals(dbPw);
            } else {
                return false;
            }
        }

        public Optional<Role> authenticate (String username, String password, HttpSession session){
            Optional<User> user = userRepo.findByUsername(username);
            if (user.isPresent() && authUser(username, password)) {

                User loggedInUser = user.get();
                session.setAttribute("loggedInUser",loggedInUser);
                return Optional.of(user.get().getRole());
            }
            return Optional.empty();
        }

        public List<User> findallRegularUsers () {
            return userRepo.findByRole(Role.USER);
        }

        public List<User> findAllAdmins () {
            return userRepo.findByRole(Role.ADMIN);
        }
    }

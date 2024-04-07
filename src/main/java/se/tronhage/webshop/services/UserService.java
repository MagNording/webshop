package se.tronhage.webshop.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.tronhage.webshop.entity.User;
import se.tronhage.webshop.enums.Role;
import se.tronhage.webshop.repository.UserRepo;
import se.tronhage.webshop.exceptions.UserAlreadyExistsException;
import se.tronhage.webshop.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void registerNewUser(String firstName, String lastName, String email, String address, String username, String password) {
        if(userRepo.existsByUsername(username)) {
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
        //newUser.setShoppingBasket(new ShoppingBasket()); // ny tom varukorg för användaren
        userRepo.save(newUser);
    }

    @Transactional
    public void updateUser(User userUpdates, String newPassword) {
        // Retrieve the user from the database based on the provided ID
        Optional<User> optionalUser = userRepo.findById(userUpdates.getId());

        if (optionalUser.isPresent()) {
            User userFromDb = getUser(userUpdates, newPassword, optionalUser);
            // Save the updated user back to the database
            userRepo.save(userFromDb);
        } else {
            // Handle the case where the user with the provided ID does not exist
            throw new UserNotFoundException("User not found with ID: " + userUpdates.getId());
        }
    }

    private static User getUser(User userUpdates, String newPassword, Optional<User> optionalUser) {
        User userFromDb = optionalUser.get();

        // Update the user details with the values from the userUpdates object
        userFromDb.setFirstName(userUpdates.getFirstName());
        userFromDb.setLastName(userUpdates.getLastName());
        userFromDb.setEmail(userUpdates.getEmail());
        userFromDb.setAddress(userUpdates.getAddress());
        userFromDb.setUsername(userUpdates.getUsername());
        userFromDb.setRole(userUpdates.getRole());

        // Uppdatera endast lösenordet om ett nytt har angetts
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            userFromDb.setPassword(newPassword);
        }
        return userFromDb;
    }


    public boolean authUser(String username, String password){
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            String dbPw = user.get().getPassword();
            return password.equals(dbPw);
        } else {
            return false;
        }
    }
    public Optional<Role> authenticate(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);

        if (user.isPresent() && authUser(username, password)) {
            return Optional.of(user.get().getRole());
        }
        return Optional.empty();
    }

    public List<User> findallRegularUsers(){
        return userRepo.findByRole(Role.USER);
    }

    public List<User> findAllAdmins(){
        return userRepo.findByRole(Role.ADMIN);
    }
}

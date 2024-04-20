package se.nording.webshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.nording.webshop.entity.Order;
import se.nording.webshop.entity.User;
import se.nording.webshop.enums.OrderStatus;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long>{
    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUser(User loggedInUser);
}

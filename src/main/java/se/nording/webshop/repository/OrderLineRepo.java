package se.nording.webshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.nording.webshop.entity.OrderLine;

public interface OrderLineRepo extends JpaRepository<OrderLine, Long> {
}

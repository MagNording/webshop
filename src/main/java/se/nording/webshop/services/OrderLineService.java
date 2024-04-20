package se.nording.webshop.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.nording.webshop.entity.Order;
import se.nording.webshop.entity.OrderLine;
import se.nording.webshop.entity.Product;
import se.nording.webshop.entity.User;
import se.nording.webshop.model.BasketItem;
import se.nording.webshop.model.ShoppingBasket;
import se.nording.webshop.repository.OrderLineRepo;

import java.util.HashSet;
import java.util.Set;

@Service
public class OrderLineService {

    private final ProductService productService;
    private final OrderLineRepo orderLineRepo;
    private final EmailService emailService;

    @Autowired
    public OrderLineService(ProductService productService, OrderLineRepo orderLineRepo,
                            EmailService emailService) {
        this.productService = productService;
        this.orderLineRepo = orderLineRepo;
        this.emailService = emailService;
    }

    public void createOrderLineFromShoppingBasket(ShoppingBasket shoppingbasket,
                                                  HttpSession session) {
        if (shoppingbasket == null) {
            throw new IllegalArgumentException(
                    "No shopping cart available to create order lines.");
        }
        Order currentOrder = (Order) session.getAttribute("currentorder");
        if (currentOrder == null) {
            throw new IllegalStateException(
                    "No current order in session.");
        }
        Set<OrderLine> orderLines = new HashSet<>(); // Create the list outside the loop
        for (BasketItem item : shoppingbasket.getItems()) {
            Product product = productService.getProductById(item.getProductId());

            OrderLine orderLine = new OrderLine(
                    item.getQuantity(),
                    item.getUnitPrice(),
                    currentOrder,
                    product
            );
            orderLineRepo.save(orderLine);
            orderLines.add(orderLine); // Add order line to the list
        }
        currentOrder.setOrderLines(orderLines); // Set all order lines to the currentOrder
        orderEmail(session);
    }

    // Skicka Orderemail
    public void orderEmail(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        emailService.sendSimpleMessage(loggedInUser.getEmail(), "Order Confirmation",
                emailService.orderMessage(loggedInUser.getUsername()));
    }
}

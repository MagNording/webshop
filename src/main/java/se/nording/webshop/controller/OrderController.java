package se.nording.webshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.nording.webshop.entity.OrderLine;
import se.nording.webshop.enums.OrderStatus;
import se.nording.webshop.entity.Order;
import se.nording.webshop.entity.User;
import se.nording.webshop.model.ShoppingBasket;
import se.nording.webshop.repository.OrderRepo;
import se.nording.webshop.services.OrderLineService;
import se.nording.webshop.services.OrderService;
import se.nording.webshop.services.ShoppingBasketManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class OrderController {

    private final OrderRepo ordersRepo;
    private final OrderService orderService;
    private final OrderLineService orderLineService;
    private final ShoppingBasketManager shoppingBasketManager;

    @Autowired
    public OrderController(OrderRepo ordersRepo, OrderService orderService,
                           OrderLineService orderLineService, ShoppingBasketManager shoppingBasketManager) {
        this.ordersRepo = ordersRepo;
        this.orderService = orderService;
        this.orderLineService = orderLineService;
        this.shoppingBasketManager = shoppingBasketManager;
    }

    // Place order and create orderlines
    @PostMapping("/place-order")
    public String placeOrder(Model m, HttpSession session){
        ShoppingBasket shoppingBasket = (ShoppingBasket) session.getAttribute("shoppingbasket");
        if (shoppingBasket == null || shoppingBasket.getItems().isEmpty()) {
            m.addAttribute("error",
                    "Your cart is emtpy Please add items before placing an order.");
            return "shoppingbasket";
        }
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            m.addAttribute("error",
                    "You must be logged in to place an order.");
            return "login";
        }
        try {
            Order order = orderService.createOrderFromShoppingBasket(shoppingBasket, loggedInUser);
            session.setAttribute("currentorder", order);

            orderLineService.createOrderLineFromShoppingBasket(shoppingBasket, session);
            m.addAttribute("orderdetails", order.getOrderLines());
            return "redirect:/confirmation";

        } catch (Exception e) {
            m.addAttribute("error",
                    "An error occurred while placing the order: " + e.getMessage());
            return "shoppingbasket";
        }
    }

    // List all orders for admin
    @GetMapping("/orders")
    public String listOrders(@RequestParam(name = "type", required = false, defaultValue = "all") String type, Model m) {
        List<Order> orders = switch (type) {
            case "active" -> orderService.findAllConfirmedOrders();
            case "shipped" -> orderService.findAllShippedOrders();
            default -> ordersRepo.findAll();
        };
        m.addAttribute("orders", orders);
        return "orders";
    }

    // Filter orders
    @PostMapping("/orders")
    public String listOrdersPost(@RequestParam(name = "filterType", required = false, defaultValue = "all") String type) {
        // You can perform additional actions if needed
        return "redirect:/orders?type=" + type;
    }

    // Update order status
    @PostMapping("/orders/update-status")
    public String updateOrders(@RequestParam("orderId") Long orderId,
                               @RequestParam("status") String status,
                               Model m) {

        Optional<Order> orderOptional = ordersRepo.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(OrderStatus.valueOf(status));

            orderService.updateOrder(order);
            m.addAttribute("order", order);
            return "redirect:/orders";
        } else {
            return "errorPage";
        }
    }

    // Show order history for logged in user
    @GetMapping("/orderhistory")
    public String showOrderHistory(HttpSession session, Model m) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            List<Order> orders = orderService.findOrdersByUser(loggedInUser);
            System.out.println("Orders found: " + orders.size());
            m.addAttribute("orders", orders);
            return "orderhistory";
        } else {
            return "errorPage";
        }
    }

    // Show order confirmation page
    @GetMapping("/confirmation")
    public String orderConfirmation(HttpSession session, Model m) {
        Order currentOrder = (Order) session.getAttribute("currentorder");

        if (currentOrder != null) {
            Set<OrderLine> orderLines = currentOrder.getOrderLines();

            m.addAttribute("orderLines", orderLines);
            m.addAttribute("orderId", currentOrder.getId());
            m.addAttribute("orderDate", currentOrder.getOrderDate());
            m.addAttribute("totalPrice", currentOrder.getTotalSum());
            m.addAttribute("message", "Order was successfully placed!");

            shoppingBasketManager.clearBasket();

            return "confirmation";
        } else {
            return "redirect:/shoppingbasket"; // Redirect to shopping basket page or handle appropriately
        }
    }

}


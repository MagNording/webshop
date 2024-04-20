package se.nording.webshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.nording.webshop.model.ShoppingBasket;
import se.nording.webshop.services.ShoppingBasketManager;

@Controller
public class ShoppingBasketController {

    private final ShoppingBasketManager basketManager;

    public ShoppingBasketController(ShoppingBasketManager basketManager) {
        this.basketManager = basketManager;
    }

    @PostMapping("/add-to-basket")
    public String addItemToBasket(@RequestParam("productId") Long productId,
                                  @RequestParam("name") String name,
                                  @RequestParam("price") String priceStr,
                                  @RequestParam("quantity") int quantity,
                                  HttpSession session, Model m) {


        int price = Integer.parseInt(priceStr);
        basketManager.addItem(productId, name, price, quantity);
        session.setAttribute("shoppingbasket", basketManager.getShoppingBasket());
        return "redirect:/shoppingbasket";
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                 @RequestParam("quantity") int quantity,
                                 HttpSession session) {


        basketManager.updateQuantity(productId, quantity);
        session.setAttribute("shoppingbasket", basketManager.getShoppingBasket());
        return "redirect:/shoppingbasket";
    }

    @PostMapping("/remove-from-basket")
    public String removeItemFromBasket(@RequestParam("productId") Long productId,
                                       HttpSession session) {


        basketManager.removeItem(productId);

        session.setAttribute("shoppingbasket", basketManager.getShoppingBasket());
        return "redirect:/shoppingbasket";
    }

    @GetMapping("/shoppingbasket")
    public String showBasket(Model m, HttpSession session) {
        ShoppingBasket shoppingbasket = (ShoppingBasket) session.getAttribute("shoppingbasket");
        if (shoppingbasket == null) {
            shoppingbasket = new ShoppingBasket();
            session.setAttribute("shoppingbasket", shoppingbasket);
        }
        m.addAttribute("shoppingbasket", shoppingbasket);
        m.addAttribute("totalprice", basketManager.calcTotalPrice(shoppingbasket)); //HERE IS WHERE I
        // WANT TO GET THE PRICE
        return "shoppingbasket";
    }
}


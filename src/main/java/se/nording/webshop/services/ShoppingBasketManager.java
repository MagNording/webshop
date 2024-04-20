package se.nording.webshop.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import se.nording.webshop.model.BasketItem;
import se.nording.webshop.model.ShoppingBasket;

import java.util.List;

@Service
@SessionScope
public class ShoppingBasketManager {

    private final ShoppingBasket shoppingBasket;

    public ShoppingBasketManager() {
        this.shoppingBasket = new ShoppingBasket();
    }

    public void addItem(Long productId,String name,int price, int quantity) {
        BasketItem item = new BasketItem(productId,name,quantity,price);
        shoppingBasket.addItem(item);
    }

    public void updateQuantity(Long productId, int quantity) {
        for (BasketItem item : shoppingBasket.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(Long productId) {
        List<BasketItem> items = shoppingBasket.getItems();
        for (int i = 0; i < items.size(); i++) {
            BasketItem item = items.get(i);
            if (item.getProductId().equals(productId)) {
                items.remove(i);
                return;
            }
        }
    }

    public int calcTotalPrice(ShoppingBasket shoppingBasket) {
        if (shoppingBasket == null) {
            throw new IllegalArgumentException(
                    "Shopping cart cannot be null for price calculation.");
        }
        int totalPrice = 0;
        for (BasketItem item : shoppingBasket.getItems()) {
            int itemPrice = item.getUnitPrice();
            int itemQuantity = item.getQuantity();
            totalPrice += itemPrice * itemQuantity;
        }
        return totalPrice;
    }

    public ShoppingBasket getShoppingBasket() {
        return shoppingBasket;
    }

    public void clearBasket() {
        shoppingBasket.getItems().clear();
    }
}


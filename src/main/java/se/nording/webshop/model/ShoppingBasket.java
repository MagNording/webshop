package se.nording.webshop.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

// Obs! Denna klass är inte en Entity-klass och ska inte mappas till en databastabell
@Component
@SessionScope
public class ShoppingBasket {
    private List<BasketItem> items = new ArrayList<>();

    public List<BasketItem> getItems() {
        return items;
    }

    public void addItem(BasketItem newItem) {
        for (BasketItem item : items) {
            if (item.getProductId().equals(newItem.getProductId())) {
                // Om produkten redan finns, uppdatera kvantiteten
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void clear() {
        this.items.clear(); // Rensar listan med BasketItems
    }
}

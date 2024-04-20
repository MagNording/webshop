package se.nording.webshop.model;

// Obs! Denna klass är inte en Entity-klass och ska inte mappas till en databastabell
public class BasketItem {
    private Long productId;
    private String productName;
    private int quantity;
    private int unitPrice;
    private int sumPrice;
    public BasketItem() {}

    public BasketItem(Long productId, String productName, int quantity, int unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getProductId() {
        return productId;

    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getTotalPrice() {
        return unitPrice * quantity;
    }


}

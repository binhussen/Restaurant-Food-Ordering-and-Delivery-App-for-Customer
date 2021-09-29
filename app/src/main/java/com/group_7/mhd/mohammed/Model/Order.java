package com.group_7.mhd.mohammed.Model;

public class Order {

    private String UserPhone;
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;
    private String Payment_method;
    private String TackAway;

    public Order() {

    }

    /*public Order(String userPhone, String produId, String produName, String quantity, String price, String discount, String image) {
        UserPhone = userPhone;
        ProduId = produId;
        ProduName = produName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
    }*/

    public Order(String userPhone, String productId, String productName, String quantity, String price, String discount, String image) {
        /*this.ID = ID;*/
        UserPhone = userPhone;
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
    }


    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

}

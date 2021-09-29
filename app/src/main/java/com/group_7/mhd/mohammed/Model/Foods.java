package com.group_7.mhd.mohammed.Model;

public class  Foods {
    private String Name;
    private String Image;
    private String Description;
    private String Price;
    private String Discount;
    private String MenuId;
    private String TackAway;

    public Foods() {

    }

    public Foods(String name, String image, String description, String price, String discount, String menuId, String tackAway) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
        TackAway = tackAway;
    }

    public String getTackAway() {
        return TackAway;
    }

    public void setTackAway(String tackAway) {
        TackAway = tackAway;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}

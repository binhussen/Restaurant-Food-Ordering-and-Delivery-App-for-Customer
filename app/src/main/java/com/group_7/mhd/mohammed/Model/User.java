package com.group_7.mhd.mohammed.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String secureCode;
    private String balance;

    public User() {

    }

    public User(String name, String password, String secureCode, String phone) {
        Name = name;
        Password = password;
        IsStaff = "false";
        this.secureCode = secureCode;
        Phone = phone;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    //alt+insert
    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String IsStaff) {
        IsStaff = IsStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
}

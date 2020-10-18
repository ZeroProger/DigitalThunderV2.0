package com.digitalthunder.user;

public class User {
    private String id, firstName, secondName, eMail, password;

    public User() {

    }

    public User(String id, String eMail, String firstName, String secondName, String password) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.eMail = eMail;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String getAllInfo() {
        return  id + " " +
                eMail + " " +
                firstName + " " +
                secondName + " " +
                password;
    }
}

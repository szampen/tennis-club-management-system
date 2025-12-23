package com.tennis.domain;

public abstract class User {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserType userType;

    public User() {}

    public User(String email, String password, String firstName, String lastName, String phoneNumber){
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        // id - to reconsider how is it supposed to work (database-wise etc.)
    }

    public abstract boolean canManageCourts();

    public Long getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public UserType getUserType(){
        return this.userType;
    }

    public void setUserType(UserType userType){
        this.userType = userType;
    }
}

package com.tennis.domain;

public class Admin extends User{
    public Admin(){
        super();
        setUserType(UserType.ADMIN);
    }

    @Override
    public boolean canManageCourts(){
        return true;
    }
}

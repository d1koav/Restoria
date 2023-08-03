package com.example.restoria;

public class AdminDataModel {
    private String nameSurname;
    private String userID;
    private String enter_key;

    public AdminDataModel(String nameSurname, String userID, String enter_key) {
        this.nameSurname = nameSurname;
        this.userID = userID;
        this.enter_key = enter_key;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public String getUserID() {
        return userID;
    }

    public String getEnter_key() {
        return enter_key;
    }

    public void setNameSurname(String nameSurname){
        this.nameSurname = nameSurname;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }
    public void setEnter_key(String enter_key){
        this.enter_key = enter_key;
    }

}

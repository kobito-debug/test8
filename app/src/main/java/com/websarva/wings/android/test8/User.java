package com.websarva.wings.android.test8;

public class User {
    public String displayname;
    public String userId;
    public String classes;
   
//ユーザーIDと所属
    public User(){
        
    }
    public String getDisplayname(){
        return displayname;
    }
    public void setDisplayname(String displayname){
        this.displayname=displayname;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public String getClasses(){
        return classes;
    }
    public void setClasses(String classes){
        this.classes=classes;
    }
  
}

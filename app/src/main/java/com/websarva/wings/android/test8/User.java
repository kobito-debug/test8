package com.websarva.wings.android.test8;

public class User {
    public String name;
    public String title;
    public String detail;
    public Double image;
    public Double latitude;
    public Double longitude;
    public String comment;
    public String othercomment;
    public String userId;

    public User(){

    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getDetail(){
        return detail;
    }
    public void setDetail(String detail){
        this.detail=detail;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public Double getImage(){return image;}
    public void setImage(double image){
        this.image=image;
    }
    public Double getLatitude(){return latitude;}
    public void setLatitude(Double latitude){
        this.latitude=latitude;
    }
    public Double getLongitude(){return longitude;}
    public void setLongitude(Double longitude){
        this.longitude=longitude;
    }
    public String getComment(){return comment;}
    public void setComment(String comment){
        this.comment=comment;
    }
    /*public String getOtherComment(){return othercomment;}
    public void setOtherComment(String othercomment){
        this.othercomment=othercomment;
    }*/
    public String getuserId(){return userId;}
    public void setUserId(String userId){
        this.userId=userId;
    }
}

package com.websarva.wings.android.test8;

public class Post {
    public String name;
    public String title;
    public String detail;
    public String image;
    public double latitude;
    public double longitude;
    public String comment;
    public String othercomment;
    public String userId;
    public String checkPrivate;

    public Post(){

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
    public String getImage(){return image;}
    public void setImage(String image){
        this.image=image;
    }
    public double getLatitude(){return latitude;}
    public void setLatitude(double latitude){
        this.latitude=latitude;
    }
    public double getLongitude(){return longitude;}
    public void setLongitude(double longitude){
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
    public String getcheckPrivate(){return checkPrivate;}
    public void setCheckPrivate(String checkPrivate){
        this.checkPrivate=checkPrivate;
    }
}


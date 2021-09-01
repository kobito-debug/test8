package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Information extends AppCompatActivity {
        StorageReference mImageDataref;
        List <String> imageList;
        Bitmap[] bitmapList;
        Bitmap bitmap;
        int n=0;
        ImageView imageView,imageView2,imageView3;
        ProgressBar progressBar;
        Post post;
        DatabaseReference reff;
        String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        TextView tvtestName=findViewById(R.id.testName);
        TextView tvtestDetail=findViewById(R.id.testDetail);
        progressBar=findViewById(R.id.pbTest);
        imageView=findViewById(R.id.imageView);
        imageView2=findViewById(R.id.imageView2);
        imageView3=findViewById(R.id.imageView3);
        progressBar.setVisibility(View.VISIBLE);
        imageList=new ArrayList<String>();
        bitmapList=new Bitmap[100];
        reff=FirebaseDatabase.getInstance().getReference("Post");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    post=data.getValue(Post.class);
                    assert post != null;
                    //title=post.getTitle();
                    //detail=post.getDetail();
                    image=post.getImage();
                    double latitude=post.getLatitude();
                    double longitude=post.getLongitude();
                    //location=new LatLng(latitude,longitude);
                    //comment=post.getComment();
                    //commentList[i]=comment;
                    //imageList[i]=image;
                    //i++;
                    //commentList.add(comment);
                    imageList.add(image);
                }
                StoragePicked();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                //データ取得失敗
                //Toast.makeText(MyPostMapsActivity.this,"Postテーブルのデータ取得に失敗しました",Toast.LENGTH_SHORT).show();
            }
        });



    }
    public void StoragePicked(){
        for(String s: imageList){
            mImageDataref =FirebaseStorage.getInstance().getReference().child("Uploads/"+s+".jpg");
            try{
                File localfile=File.createTempFile("tempfile",".jpg");
                mImageDataref.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        bitmap=BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        //Bitmap mbitmap=Bitmap.createScaledBitmap(s,s.getWidth()/10,s.getHeight()/10,true);
                        bitmapList[n]=bitmap;
                        n++;
                        if(n==1){
                            imageView.setImageBitmap(bitmapList[n-1]);
                        }else if(n==2){
                            imageView2.setImageBitmap(bitmapList[n-1]);
                        }else{
                            imageView3.setImageBitmap(bitmapList[n-1]);
                        }
                        //setMarker(title,detail,location);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //Toast.makeText(MyPostMapsActivity.this,"写真の取得に失敗しました_"+image+".jpg",Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        progressBar.setVisibility(View.GONE);
    }

}
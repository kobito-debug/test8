package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//自分のマップ・作製例
public class MyPostMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int i=1;
    private long maxId;
    private DatabaseReference reff;
    private StorageReference mImageDataref;
    private Marker[] markerList;
    private String[] commentList,imageList;
    private Bitmap[]  bitmapList;
    Post post;
    Query query;
    Bitmap bitmap;
    private int n=0;
    String username;
    int count=1;
    DatabaseReference reffPost;
    private FirebaseAuth mAuth;
    String userID;
    String intentname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent=getIntent();
        username=intent.getStringExtra("name");
        intentname=intent.getStringExtra("intentname");
        Toast.makeText(MyPostMapsActivity.this,"ようこそ"+username,Toast.LENGTH_SHORT).show();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("マップ画面");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        maxId=intent.getLongExtra("maxId",0);
        commentList=new String[100];
        //otherList=new ArrayList<String>();
        imageList=new String[100];
        markerList=new Marker[100];
        bitmapList=new Bitmap[100];
        mAuth= FirebaseAuth.getInstance();
        userID=mAuth.getCurrentUser().getUid();
    }

    public void setMarker(String title,String detail,LatLng location,Bitmap bitmap){
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());
        MarkerOptions options=new MarkerOptions();
        options.position(location);
        options.title(title);
        options.snippet(detail);
        System.out.println("position:"+location+",title:"+title+",detail:"+detail);
        BitmapDescriptor icon;
        switch (title){
            case "見えにくい場所":
                icon= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                options.icon(icon);
                break;
            case "入りやすい場所":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                options.icon(icon);
                break;
            case "治安が悪い場所":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                options.icon(icon);
                break;
            case "避難できる場所":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                options.icon(icon);
                break;
            case "まちの残したい場所":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                options.icon(icon);
                break;
            case "安全への取り組み":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                options.icon(icon);
                break;
            case "その他":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                options.icon(icon);
                break;
        }
        Marker marker=mMap.addMarker(options);
        markerList[n-1]=marker;
        marker.showInfoWindow();
        /*if(n==count){
            Toast.makeText(MyPostMapsActivity.this,"マーカーの設置が終わりました",Toast.LENGTH_LONG).show();
        }*/
        count++;
    }

    private class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter{
        private final View mWindow;
        public CustomInfoAdapter(){
            mWindow=getLayoutInflater().inflate(R.layout.info_window_layout,null);
        }

        @Override
        public View getInfoWindow(Marker marker){
            render(marker,mWindow);
            return mWindow;
        }
        @Override
        public View getInfoContents(Marker marker){
            return null;
        }

        private void render(Marker marker,View view){
            TextView tvmapTitle=view.findViewById(R.id.tvmapTitle);
            TextView tvmapDetail=view.findViewById(R.id.tvmapDetail);
            TextView tvmapComment=view.findViewById(R.id.tvmapComment);
            ImageView ivMapCamera=view.findViewById(R.id.ivMapCamera);
            TextView tvDate=view.findViewById(R.id.tvDate);
            for(int a=0;a<100;a++){
                if(marker.equals(markerList[a])){
                    ivMapCamera.setImageBitmap(bitmapList[a]);
                    tvmapComment.setText(commentList[a]);
                }
            }
            tvmapTitle.setText(marker.getTitle());
            tvmapDetail.setText(marker.getSnippet());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // LatLng present=new LatLng(platitude,plongitude);
        LatLng test=new LatLng(35.68944,139.69167);
        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(test,7));

        post=new Post();
        reffPost=FirebaseDatabase.getInstance().getReference().child("Post");
        if(intentname.equals("自分のマップ")){
            query=reffPost.orderByChild("userId").equalTo(userID);
        }else if(intentname.equals("作製例")){
            query=reffPost.orderByChild("userId").equalTo("efzp5DnBouhB9zY1DnropkVgnz12");
        }
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                post=snapshot.getValue(Post.class);
                String title=post.getTitle();
                String detail=post.getDetail();
                String image=post.getImage();
                double latitude=post.getLatitude();
                double longitude=post.getLongitude();
                LatLng location=new LatLng(latitude,longitude);
                String comment=post.getComment();
                commentList[i]=comment;
                imageList[i]=image;
                i++;
                StoragePicked(image,title,detail,comment,location);

            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void StoragePicked(String image,String title,String detail,String comment,LatLng location){
        //Storageから画像取得処理
        //for(String s: imageList){
            mImageDataref =FirebaseStorage.getInstance().getReference().child("Uploads/"+image+".jpg");
            try{
                File localfile=File.createTempFile("tempfile",".jpg");
                mImageDataref.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        bitmap=BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        //Bitmap mbitmap=Bitmap.createScaledBitmap(s,s.getWidth()/10,s.getHeight()/10,true);
                        bitmapList[n]=bitmap;
                        n++;
                        setMarker(title,detail,location,bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MyPostMapsActivity.this,"写真の取得に失敗しました_"+image+".jpg",Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        //}

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId();
        if(itemId==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
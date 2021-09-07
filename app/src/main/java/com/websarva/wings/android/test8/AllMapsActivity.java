package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.websarva.wings.android.test8.databinding.ActivityAllMapsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.websarva.wings.android.test8.MainActivity.getNowDateTime;

public class AllMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int i=1;
    private long maxId;
    private DatabaseReference reff;
    private StorageReference mImageDataref;
    private Marker[] markerList;
    private String[] imageList;
    private ArrayList<String> commentList;
    private Bitmap[]  bitmapList;
    Post post;
    Bitmap bitmap;
    private int n=0;
    private int m=0;
    private int count=1;
    private final String username="高専太郎";

    public AllMapsActivity() {
    }
    // private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent=getIntent();
        //maxId=intent.getLongExtra("maxId",0);
        //commentList=new String[100];
        commentList=new ArrayList<String>() ;
        //otherList=new ArrayList<String>();
        imageList=new String[100];
        markerList=new Marker[100];
        bitmapList=new Bitmap[100];
    }

    public void setMarker(String title,String detail,LatLng location,Bitmap bitmap,String comment){
        mMap.setInfoWindowAdapter(new AllMapsActivity.CustomInfoAdapter());
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
        markerList[m]=marker;
        marker.showInfoWindow();
        if(n<count){
            Toast.makeText(AllMapsActivity.this,"マーカーの設置が終わりました",Toast.LENGTH_LONG).show();
        }
        count++;
        m++;
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
                    //String comment=commentList[a];
                    String comment=commentList.get(a);
                    ivMapCamera.setImageBitmap(bitmapList[a]);
                    tvmapComment.setText(comment);
                    //System.out.println("bitmap:"+bitmapList[a]+",comment:"+commentList[a+1]);
                }
                //if(marker.equals())
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

        //データベース取得処理
        reff=FirebaseDatabase.getInstance().getReference("Post");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    post=data.getValue(Post.class);
                    assert post != null;
                    //System.out.println("id="+id);
                    String name=post.getName();
                    if(name.equals(username)) {
                        String title = post.getTitle();
                        String detail = post.getDetail();
                        String image = post.getImage();
                        double latitude = post.getLatitude();
                        double longitude = post.getLongitude();
                        LatLng location = new LatLng(latitude, longitude);
                        String comment = post.getComment();
                        //commentList[i]=comment;
                        commentList.add(comment);
                        imageList[i] = image;
                        i++;
                        StoragePicked(image, title, detail, comment, location);
                        //System.out.println(commentList[i-1]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                //データ取得失敗
                Toast.makeText(AllMapsActivity.this,"Postテーブルのデータ取得に失敗しました",Toast.LENGTH_SHORT).show();
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
                    Bitmap bm=Bitmap.createScaledBitmap(bitmap,480,410,true);
                    bitmapList[n]=bm;
                    n++;
                    setMarker(title,detail,location,bm,comment);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(AllMapsActivity.this,"写真の取得に失敗しました_"+image+".jpg",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
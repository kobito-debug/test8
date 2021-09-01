package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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

public class AllMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private DatabaseReference reff;
    private StorageReference mImageDataref;
    Post post;
    private Marker[] markerList;
    private String[] commentList,otherList,imageList,titleList,detailList;
    private Bitmap[]  bitmapList;
    LatLng[] locationList;
    int a=0;//bitmaplistの配列番号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        commentList=new String[100];
        otherList=new String[100];
        imageList=new String[100];
        markerList=new Marker[100];
        bitmapList=new Bitmap[100];
        locationList=new LatLng[100];
        titleList=new String[100];
        detailList=new String[100];

    }

    private void setUpMap(String title,String detail,String comment,String image,LatLng location){
        //LatLng location=new LatLng(35.697261,139.774728);
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());
        MarkerOptions options=new MarkerOptions();
        options.position(location);
        options.title(title);
        options.snippet(detail);
        BitmapDescriptor icon= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        options.icon(icon);
        Marker marker=mMap.addMarker(options);
        //markerList[b]=marker;
        marker.showInfoWindow();
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
            TextView tvmaptitle=findViewById(R.id.tvmapTitle);
            TextView tvmapdetail=findViewById(R.id.tvmapDetail);
            TextView tvmapcomment=findViewById(R.id.tvmapComment);
            ImageView ivmap=findViewById(R.id.ivMapCamera);
            /*for(int b=0;b<=100;b++){
                //ivmap.setImageBitmap(bitmapList[b]);
                tvmapcomment.setText(commentList[b]);
            }*/
            tvmaptitle.setText(marker.getTitle());
            tvmapdetail.setText(marker.getSnippet());
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37, 140);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        reff= FirebaseDatabase.getInstance().getReference("Post");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int i=0;//Post内のデータを取得し、配列に入れるときの番号
                for(DataSnapshot data: snapshot.getChildren()){
                    post=data.getValue(Post.class);
                    assert post != null;
                    String title=post.getTitle();
                    String detail=post.getDetail();
                    String image=post.getImage();
                    double latitude=post.getLatitude();
                    double longitude=post.getLongitude();
                    LatLng location=new LatLng(latitude,longitude);
                    String comment=post.getComment();
                    commentList[i]=comment;
                    imageList[i]=image;
                    locationList[i]=location;
                    titleList[i]=title;
                    detailList[i]=detail;
                    i++;
                    Toast.makeText(AllMapsActivity.this,i+"番目のpostテーブルのデータを取得",Toast.LENGTH_SHORT).show();
                    System.out.println(i+"番目のデータを取得");
                    setUpMap(title,detail,comment,image,location);
                }
                //if(locationList!=null)StoragePicked();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                //データ取得失敗
                Toast.makeText(AllMapsActivity.this,"Postテーブルのデータ取得に失敗しました",Toast.LENGTH_SHORT).show();
            }
        });

       /* for(int n=0;n<100;n++) {
            mImageDataref = FirebaseStorage.getInstance().getReference().child("Uploads/" + imageList[n] + ".jpg");
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                mImageDataref.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        //Bitmap mbitmap=Bitmap.createScaledBitmap(s,s.getWidth()/10,s.getHeight()/10,true);
                        bitmapList[a]=bitmap;
                        a++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AllMapsActivity.this, "写真の取得に失敗しました_" +a + "番目", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

public class MyPostMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int i=1;
    private long maxId;
    private double platitude;
    private double plongitude;
    private DatabaseReference reff,mImageDataref;
    private TextView tvmapTitle,tvmapDetail,tvmapComment,tvInfoOther;
    private ImageView ivMapCamera;
    private String commentList[],otherList[];
    private Marker markerList[];
    private String imageList[];
    Post post;
    String title;
    String detail;
    LatLng location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvmapTitle=findViewById(R.id.tvmapTitle);
        tvmapDetail=findViewById(R.id.tvmapDetail);
        tvmapComment=findViewById(R.id.tvmapComment);
        tvInfoOther=findViewById(R.id.tvInfoOther2);
        ivMapCamera=findViewById(R.id.ivMapCamera);
        Intent intent=getIntent();
        maxId=intent.getLongExtra("maxId",0);
        commentList=new String[100];
        otherList=new String[100];
        markerList=new Marker[100];
        imageList=new String[100];
    }

    public void setMarker(String title,String detail,LatLng location){
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
            case "その他":
                icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                options.icon(icon);
                break;
        }
        //options.icon(icon);
        Marker marker=mMap.addMarker(options);
        markerList[i-1]=marker;
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
            TextView tvmapTitle=view.findViewById(R.id.tvmapTitle);
            TextView tvmapDetail=view.findViewById(R.id.tvmapDetail);
            TextView tvmapComment=view.findViewById(R.id.tvmapComment);
            ImageView ivMapCamera=view.findViewById(R.id.ivMapCamera);
            TextView tvDate=view.findViewById(R.id.tvDate);
            //配列から一つずつ取り出し、マーカーとウィンドウを設置
            for(int n=0;n<100;n++){
                if(marker.equals(markerList[n])){
                    String comment=commentList[n];
                    tvmapComment.setText(comment);
                    //imageもいれる
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
        LatLng present=new LatLng(platitude,plongitude);
        LatLng test=new LatLng(37,147);
        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(test,7));

        //データベース取得処理
        reff=FirebaseDatabase.getInstance().getReference("User");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    post=data.getValue(Post.class);
                    title=post.getTitle();
                    detail=post.getDetail();
                    String image=post.getImage();
                    Double latitude=post.getLatitude();
                    Double longitude=post.getLongitude();
                    location=new LatLng(latitude,longitude);
                    String comment=post.getComment();
                    commentList[i]=comment;
                    imageList[i]=image;
                    i++;
                    setMarker(title,detail,location);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                //データ取得失敗
                Toast.makeText(MyPostMapsActivity.this,"データ取得に失敗しました",Toast.LENGTH_SHORT).show();
            }
        });

        //Storageから画像取得処理
        mImageDataref =FirebaseDatabase.getInstance().getReference("uploads");
        mImageDataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload upload=postSnapshot.getValue(Upload.class);
                    //リストにuploadを入れる
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(MyPostMapsActivity.this,"画像の取得に失敗しました",Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*@Override
    public void onInfoWindowClick(Marker marker){
        //投稿編集画面へ移動
    }*/
}
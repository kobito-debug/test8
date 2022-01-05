package com.websarva.wings.android.test8;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.websarva.wings.android.test8.databinding.ActivityMapsConfirmBinding;

public class MapsConfirmActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsConfirmBinding binding;
    private LatLng location;
    private String title;
    private String Detail;
    private String Comment;
    private String snippet;
    private Bitmap bmp;
    private byte[] imageByte;
    private double _latitude;
    private double _longitude;
    private LatLng postLocation;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Detail = intent.getStringExtra("Detail");
        Comment = intent.getStringExtra("Comment");
        String Latitude = intent.getStringExtra("Latitude");
        String Longitude = intent.getStringExtra("Longitude");
        title=intent.getStringExtra("title");
        imageByte=intent.getByteArrayExtra("imageByte");
        //path=intent.getStringExtra("imagePath");
        _latitude=Double.parseDouble(Latitude);
        _longitude=Double.parseDouble(Longitude);
    }
    public void setMarker(LatLng location){
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());
        MarkerOptions options=new MarkerOptions();
        options.position(location);
        options.title(title);
        options.snippet(Detail);
        Marker marker=mMap.addMarker(options);
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
            tvmapTitle.setText(marker.getTitle());
            tvmapDetail.setText(marker.getSnippet());
            tvmapComment.setText(Comment);
            if(bmp!=null){
                ivMapCamera.setImageBitmap(bmp);
            }
            /*if(!(bmpPath.equals(""))){
                ivMapCamera.setImageBitmap(bmpPath);
            }*/
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
        //LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        postLocation=new LatLng(_latitude,_longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(postLocation,10));
        bmp=null;
        bytes=imageByte;
        if(bytes != null){
            bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        /*if (!(path.equals(""))) {
            bmpPath=BitmapFactory.decodeFile(path);
            int width=bmpPath.getWidth()/3;
            int height=bmpPath.getHeight()/3;
            //Bitmap image2=Bitmap.createScaledBitmap(bmpPath,width,height,false);
        }*/
        setMarker(postLocation);
    }
}
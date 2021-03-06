package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private String title="";
    RadioGroup rgSelect;
    User user;
    Post post;
    long maxid=0;
    DatabaseReference reffpost;
    DatabaseReference reffUser;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    String userID;
    String username;
    String imageName;
    LocationManager locationManager;
    private static final int PICK_IMAGE_REQUEST=1;
    private static final int REQUEST_GALLERY=0;
    private static final int RESULT_CAMERA=1001;
    private ImageView imageView;
    private String path;
    private String platitude="0",plongitude="0";
    private Uri mImageUri;
    private StorageReference mStorageref;
    private DatabaseReference stDataref;
    private ProgressBar progressBar;
    private StorageTask mUploadTask;
    private String classes;

    private String image;
    Button btPost;
    private int clickCount=0;
    String extraname;
    private byte[] imageByte;
    private RadioGroup rg;
    private String checkPrivate=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        extraname=intent.getStringExtra("name");
       // classes=intent.getStringExtra("classes");
        TextView tvExample = findViewById(R.id.tvExample);
        //???????????????
        final String strHardSee = "?????????????????????????????????????????????????????????????????????";
        final String strEasyEnter = "????????????????????????????????????????????????????????????????????????????????????";
        final String strBad = "?????????????????????????????????????????????????????????????????????????????????????????????";
        final String strEscape = "???????????????110?????????";
        final String strAction="???????????????????????????????????????";
        maxid=0;

        //UID?????????
        mAuth=FirebaseAuth.getInstance();
        userID=mAuth.getCurrentUser().getUid();
        username=mAuth.getCurrentUser().getDisplayName();
        System.out.println(userID+":"+username);
        Toast.makeText(MainActivity.this,"????????????"+username+"?????????",Toast.LENGTH_SHORT).show();
        //Post????????????
        post=new Post();
        reffpost=FirebaseDatabase.getInstance().getReference().child("Post");
        reffpost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid=(snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        //Storage
        mStorageref= FirebaseStorage.getInstance().getReference("Uploads");
        progressBar=(ProgressBar)findViewById(R.id.pbMain);
        btPost = findViewById(R.id.btPost);
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostClicked(view);
            }
        });
        stDataref=FirebaseDatabase.getInstance().getReference("Uploads");

        // ??????????????????????????????
        user=new User();
        reffUser=FirebaseDatabase.getInstance().getReference().child("User");
        Query query=reffUser.orderByChild("userId").equalTo(userID);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                user=snapshot.getValue(User.class);
                classes=user.getClasses();
                Toast.makeText(MainActivity.this,"?????????????????????"+classes,Toast.LENGTH_LONG).show();
                System.out.println("????????????"+classes);
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
                Log.d("Myapps","user not found.");
            }
        });

        //button
        Button btPresent = findViewById(R.id.btPresent);
        btPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPresentClicked(view);
            }
        });

        Button btShowMap = findViewById(R.id.btShowMap);
        btShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapShowCurrentButtonClick(view);
            }
        });

        Button btSearch=findViewById(R.id.btSearch);
        btSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    onSearchClicked(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button btClear=findViewById(R.id.btClear);
        btClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onClearButtonClicked(view);
            }
        });

        Button btInput=findViewById(R.id.btInput);
        btInput.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onInputClicked(view);
            }
        });

        Button btImageupload=findViewById(R.id.btImageupload);
        btImageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                clickCount=1;
            }
        });
        Button btImageupload2=findViewById(R.id.btImageupload2);
        btImageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                clickCount=1;
            }
        });
        Spinner spTitle=findViewById(R.id.sptitle);
        /*spTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                title=spTitle.getSelectedItem().toString();
                Toast.makeText(MainActivity.this,title,Toast.LENGTH_SHORT).show();
            }
        });*/
        spTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                title=spTitle.getSelectedItem().toString();
                //Toast.makeText(MainActivity.this,title,Toast.LENGTH_SHORT).show();
                if(title.equals("?????????????????????")){
                    tvExample.setText(strHardSee);
                }else if(title.equals("?????????????????????")){
                    tvExample.setText(strEasyEnter);
                }else if(title.equals("?????????????????????")){
                    tvExample.setText(strBad);
                }else if(title.equals("?????????????????????")){
                    tvExample.setText(strEscape);
                }else if(title.equals("????????????????????????")){
                    tvExample.setText(strAction);
                }else{
                    tvExample.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rg=findViewById(R.id.rgPrivate);
        int rgId=rg.getCheckedRadioButtonId();
        if(rgId==R.id.rbOn){
            checkPrivate="true";
        }else if(rgId==R.id.rbOff){
            checkPrivate="false";
        }

        //??????????????????
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
        }else{
            locationStart();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,50,(LocationListener)MainActivity.this);
        }
    }

    //??????????????????????????????????????????
    public void onPostClicked(View View){
        //id?????????Othercomment???image????????????
        TextView tvLatitude=findViewById(R.id.tvLatitude);
        TextView tvLongitude=findViewById(R.id.tvLongitude);
        EditText etDetail=findViewById(R.id.etDetail);
        EditText etComment=findViewById(R.id.etComment);
        Double latitude=0.0;
        Double longitude=0.0;
        try{
            latitude=Double.parseDouble(tvLatitude.getText().toString());
            longitude=Double.parseDouble(tvLongitude.getText().toString());
        }catch (NumberFormatException e){
            System.out.println("???????????????????????????????????????????????????");
        }
        String detail=etDetail.getText().toString();
        String comment=etComment.getText().toString();
        //Double image=0.0;
        String othercomment="";
        image=String.valueOf(mImageUri);
        int rgId=rg.getCheckedRadioButtonId();
        if(rgId==R.id.rbOn){
            checkPrivate="true";
        }else if(rgId==R.id.rbOff){
            checkPrivate="false";
        }else{
            checkPrivate=null;
        }
        System.out.println("image="+image);
        if(title.equals("---????????????????????????---") || detail.equals("") || latitude==0.0 || longitude==0.0 || clickCount<1 || checkPrivate==null){
            Toast.makeText(MainActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
        }else{
            //???????????????
            post.setName(username);
            post.setTitle(title);
            post.setDetail(detail);
            post.setLatitude(latitude);
            post.setLongitude(longitude);
            if(comment.equals("")){
                post.setComment("??????");
            }else{
                post.setComment(comment);
            }
            if(image==null){
                image="null";
            }
            post.setImage(imageName);
            // user.setOtherComment(othercomment);
            post.setUserId(userID);
            rg=findViewById(R.id.rgPrivate);
            post.setCheckPrivate(checkPrivate);
            maxid++;
            reffpost.child(String.valueOf(maxid+1)).setValue(post);
            Toast.makeText(MainActivity.this,"????????????????????????",Toast.LENGTH_LONG).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr =getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    //?????????Storage????????????
    private void uploadFile(){
        if(mImageUri!=null){
            //StorageReference fileReference=mStorageref.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            imageName=getNowDateTime();
            StorageReference fileReference=mStorageref.child(imageName+"."+getFileExtension(mImageUri));
            stDataref=FirebaseDatabase.getInstance().getReference("uploads");
            progressBar.setVisibility(View.VISIBLE);
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    },500);
                    Toast.makeText(MainActivity.this,"????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    //image=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    Upload upload=new Upload(imageName,taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                    String uploadId=stDataref.push().getKey();
                    stDataref.child(uploadId).setValue(upload);
                    //reffpost.child(String.valueOf(maxid+1)).setValue(post);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });
        }else{
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
        }
    }
    //??????????????????????????????????????????????????????
    public void onGyarallyClicked(View view){
        imageView=findViewById(R.id.ivGallery);
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_GALLERY);
        //openFileChooser();
    }
    //????????????????????????????????????????????????
    public void onCameraImageClick(View view){
        //WRITE_EXTERNAL_STORAGE???????????????????????????????????????
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //WRITE_EXTERNAL_STORAGE???????????????????????????????????????????????????????????????????????????????????????2000????????????
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        //?????????????????????yyyyMMddHHmmss?????????????????????????????????????????????????????????
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //???????????????????????????
        Date now = new Date(System.currentTimeMillis());
        //?????????????????????????????????yyyyMMddHHmmss?????????????????????????????????????????????
        String nowStr = dateFormat.format(now);
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        String fileName = "UseCameraActivityPhoto_" + nowStr +".jpg";

        //ContentValues??????????????????????????????
        ContentValues values = new ContentValues();
        //?????????????????????????????????
        values.put(MediaStore.Images.Media.TITLE, fileName);
        //???????????????????????????????????????
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        //ContentResolver??????????????????????????????
        ContentResolver resolver = getContentResolver();
        //ContentResolver????????????URI??????????????????????????????
        mImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Intent??????????????????????????????
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Extra???????????????_imageUri????????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent,RESULT_CAMERA);
    }

    private void locationStart(){
        Log.d("debug","locationStart()");
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        if(locationManager!=null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d("debug","location manager Enabled");
        }else{
            Intent settingIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingIntent);
            Log.d("debug","not gpsEnabled");
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
            Log.d("debug","checkSelfPermission false");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,50, (LocationListener) this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       //???????????????????????????????????????????????????????????????
        if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK) {
            imageView=findViewById(R.id.ivCamera);
            imageView.setImageURI(mImageUri);
            imageByte=imageViewToByte(imageView);
        }
        //???????????????
        if(requestCode==REQUEST_GALLERY && resultCode==RESULT_OK){
            mImageUri=null;
            if(data!=null){
                mImageUri=data.getData();
            }
            try {
                BufferedInputStream inputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                int width=image.getWidth()/4;
                int height=image.getHeight()/4;
                Bitmap image2=Bitmap.createScaledBitmap(image,width,height,false);
                imageView.setImageBitmap(image2);
                imageByte=imageViewToByte(imageView);
            } catch (Exception e) {

            }
            // ContentResolver????????????????????????????????????
            ContentResolver cr = getContentResolver();
            String[] columns = {
                    MediaStore.Images.Media.DATA
            };
            Cursor c = cr.query(data.getData(), columns, null, null, null);
            int column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            c.moveToFirst();
            path = c.getString(column_index);
            Log.v("test", "path=" + path);
        }
    }

    public static byte[] imageViewToByte(ImageView image){
        Bitmap bitByte=((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitByte.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray=stream.toByteArray();
        return byteArray;
    }

    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    public void onMapShowCurrentButtonClick(View view){
        TextView tvLatitude=findViewById(R.id.tvLatitude);
        TextView tvLongitude=findViewById(R.id.tvLongitude);
        Double Latitude=Double.parseDouble(tvLatitude.getText().toString());
        Double Longitude=Double.parseDouble(tvLongitude.getText().toString());
        //????????????????????????????????????????????????????????????????????????????????????URI?????????????????????
        String uriStr = "geo:" + Latitude + "," + Longitude;
        //URI???????????????URI??????????????????????????????
        Uri uri = Uri.parse(uriStr);
        //Intent??????????????????????????????
        Intent intentMaps = new Intent(Intent.ACTION_VIEW, uri);
        //?????????????????????????????????
        startActivity(intentMaps);
    }

    public void onInputClicked(View view){//??????????????????????????????????????????????????????????????????????????????
        EditText etLatitude=findViewById(R.id.etSetLatitude);
        EditText etLongitude=findViewById(R.id.etSetLongitude);
        TextView tvLatitude=findViewById(R.id.tvLatitude);
        TextView tvLongitude=findViewById(R.id.tvLongitude);
        String setLatitude=etLatitude.getText().toString();
        String setLongitude=etLongitude.getText().toString();
        tvLatitude.setText(setLatitude);
        tvLongitude.setText(setLongitude);
    }
    public void onSearchClicked(View view) throws IOException{
        EditText etSearch=findViewById(R.id.etLocation);
        TextView tvLatitude=findViewById(R.id.tvLatitude);
        TextView tvLongitude=findViewById(R.id.tvLongitude);
        double latitude=0;
        double longitude=0;
        //??????????????????????????????
        String Search2str=etSearch.getText().toString();
        //??????????????????????????????????????????????????????????????????
        Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
        //??????????????????????????????????????????
        int maxResults=1;
        //???????????????????????????
        List<Address> lstAddr;
        //?????????????????????
        lstAddr=geocoder.getFromLocationName(Search2str,maxResults);
        if(lstAddr!=null && lstAddr.size()>0){
            //?????????????????????
            Address addr=lstAddr.get(0);
            latitude=addr.getLatitude();
            longitude=addr.getLongitude();
            tvLatitude.setText(Double.toString(latitude));
            tvLongitude.setText(Double.toString(longitude));
        }
    }

    public void onPresentClicked(View view){
        TextView tvLat=findViewById(R.id.tvLatitude);
        TextView tvLng=findViewById(R.id.tvLongitude);
        tvLat.setText(platitude);
        tvLng.setText(plongitude);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        if(requestCode==1000){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("debug","checkSelfPermission true");
                locationStart();
            }else{
                Toast toast=Toast.makeText(MainActivity.this,"?????????????????????????????????",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider,int status,Bundle extras){

    }
    @Override
    public void onLocationChanged(Location location){
        Button btPresent=findViewById(R.id.btPresent);
        platitude=String.valueOf(location.getLatitude());
        plongitude=String.valueOf(location.getLongitude());
        Toast.makeText(MainActivity.this,"???????????????????????????????????????",Toast.LENGTH_SHORT).show();
        btPresent.setEnabled(true);
    }

    @Override
    public void onProviderEnabled(String provider){}

    @Override
    public  void onProviderDisabled(String provider){}

    public void onClearButtonClicked(View view){
        EditText Detail=findViewById(R.id.etDetail);
        Detail.setText("");
        Spinner spinner=findViewById(R.id.sptitle);
        spinner.setSelection(0);
        ImageView ivCamera=findViewById(R.id.ivCamera);
        ivCamera.setImageResource(android.R.drawable.ic_menu_camera);
        ImageView ivGallery=findViewById(R.id.ivGallery);
        ivGallery.setImageResource(android.R.drawable.ic_menu_gallery);
        EditText etSearch=findViewById(R.id.etLocation);
        etSearch.setText("");
        EditText etsetLat=findViewById(R.id.etSetLatitude);
        etsetLat.setText("");
        EditText etsetLon=findViewById(R.id.etSetLongitude);
        etsetLon.setText("");
        EditText etComment=findViewById(R.id.etComment);
        etComment.setText("");
        TextView tvLat=findViewById(R.id.tvLatitude);
        TextView tvLng=findViewById(R.id.tvLongitude);
        tvLat.setText("");
        tvLng.setText("");
        clickCount=0;
        Toast.makeText(MainActivity.this,"???????????????????????????????????????",Toast.LENGTH_SHORT).show();
    }
    public static String getNowDate(){
        final DateFormat df=new SimpleDateFormat("yyyyMMdd");
        final Date date=new Date(System.currentTimeMillis());
        return df.format(date);
    }

    public static String getNowTime(){
        final DateFormat df=new SimpleDateFormat("HHmmss");
        final Date time=new Date(System.currentTimeMillis());
        return df.format(time);
    }

    //????????????????????????
    public static String getNowDateTime(){
        String now=getNowDate()+getNowTime();
        return now;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId();
        if(classes.equals("??????")){
            //?????????????????????????????????
            switch (itemId){
                //???????????????????????????
                case R.id.menuSeeMyMap:
                    Intent mypostIntent=new Intent(MainActivity.this,MyPostMapsActivity.class);
                    mypostIntent.putExtra("name",username);
                    mypostIntent.putExtra("intentname","??????????????????");
                    startActivity(mypostIntent);
                    break;
                //?????????????????????
                case R.id.menuInformation:
                    Intent informationIntent=new Intent(MainActivity.this,Information.class);
                    startActivity(informationIntent);
                    break;
                //??????????????????
                case R.id.menuMapExample:
                    Intent allpostIntent=new Intent(MainActivity.this,MyPostMapsActivity.class);
                    allpostIntent.putExtra("intentname","?????????");
                    startActivity(allpostIntent);
                    break;
                //?????????????????????????????????????????????
                case R.id.menuSeeAllMap:
                    Toast.makeText(MainActivity.this,"????????????????????????????????????????????????",Toast.LENGTH_LONG).show();
                    break;
            }
        }else{
            //??????????????????????????????????????????
            switch (itemId){
                //???????????????????????????
                case R.id.menuSeeMyMap:
                    Intent mypostIntent=new Intent(MainActivity.this,MyPostMapsActivity.class);
                    mypostIntent.putExtra("name",username);
                    startActivity(mypostIntent);
                    break;
                //?????????????????????
                case R.id.menuInformation:
                    Intent informationIntent=new Intent(MainActivity.this,Information.class);
                    startActivity(informationIntent);
                    break;
                //??????????????????
                case R.id.menuMapExample:
                    Intent ExampleIntent=new Intent(MainActivity.this,AllMapsActivity.class);
                    startActivity(ExampleIntent);
                    break;
                //?????????????????????????????????????????????
                case R.id.menuSeeAllMap:
                    Intent AllMapIntent=new Intent(MainActivity.this,AllMapsActivity.class);
                    startActivity(AllMapIntent);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
   }




/*
????????????????????????????????????????????????
https://www.youtube.com/watch?v=tbh9YaWPKKs

https://www.youtube.com/watch?v=eGWu0-0TWFI
* */
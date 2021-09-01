package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private String title="";
    RadioGroup rgSelect;
    User user;
    Post post;
    long maxid=0;
    DatabaseReference reffpost;
    private FirebaseAuth mAuth;
    String userID;
    String username;
    String imageName;
    private static final int PICK_IMAGE_REQUEST=1;
    private static final int REQUEST_GALLERY=0;
    private static final int RESULT_CAMERA=1001;
    private ImageView imageView;
    private String path;

    private Uri mImageUri;
    private StorageReference mStorageref;
    private DatabaseReference stDataref;
    private ProgressBar progressBar;
    private StorageTask mUploadTask;

    private String image;
    Button btPost;
    private int clickCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvExample = findViewById(R.id.tvExample);
        //本を参考に
        final String strHardSee = "例）街灯が少なくて夜間暗くなる、人通りが少ない";
        final String strEasyEnter = "例）入口が広い、柵がない、セキュリティが設置されていない";
        final String strBad = "例）落書きがされている、ゴミが捨てられている、ものが壊れている";
        final String strEscape = "例）子ども110番の家";
        final String strAction="例）清掃活動、花壇の水やり";
        maxid=0;

        //Postテーブル
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

        mStorageref= FirebaseStorage.getInstance().getReference("Uploads");
        progressBar=(ProgressBar)findViewById(R.id.pbMain);
        btPost = findViewById(R.id.btPost);
        //btPost.setEnabled(false);
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostClicked(view);
            }
        });
        stDataref=FirebaseDatabase.getInstance().getReference("Uploads");

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

        rgSelect = findViewById(R.id.rgSelect);
        //int checkedId=rgSelect.getCheckedRadioButtonId();
        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                switch (checkedId) {
                    case R.id.rbHardSee:
                        tvExample.setText(strHardSee);
                        title="見えにくい場所";
                        break;

                    case R.id.rbEasyEnter:
                        tvExample.setText(strEasyEnter);
                        title="入りやすい場所";
                        break;

                    case R.id.rbBad:
                        tvExample.setText(strBad);
                        title="治安が悪い場所";
                        break;

                    case R.id.rbEscape:
                        tvExample.setText(strEscape);
                        title="避難できる場所";
                        break;
                    case R.id.rbAttraction:
                        tvExample.setText("");
                        title="まちの残したい場所";
                        break;
                    case R.id.rbAction:
                        tvExample.setText(strAction);
                        title="安全への取り組み";
                        break;
                    case R.id.rbOther:
                        tvExample.setText("");
                        title="その他";
                        break;
                }
            }
        });

    }

    //入力内容をデータベースに登録
    public void onPostClicked(View View){
        //id取得。Othercommentとimageもやる。
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
            System.out.println("緯度または経度の入力がありません。");
        }
        String detail=etDetail.getText().toString();
        String comment=etComment.getText().toString();
        //Double image=0.0;
        String othercomment="";
        image=String.valueOf(mImageUri);
        System.out.println("image="+image);
        if(title.equals("") || detail.equals("") || latitude==0.0 || longitude==0.0 || clickCount<1 ){
            Toast.makeText(MainActivity.this, "入力が終わっていません", Toast.LENGTH_SHORT).show();
        }else{
            //UIDの取得
            mAuth=FirebaseAuth.getInstance();
            userID=mAuth.getCurrentUser().getUid();
            username=mAuth.getCurrentUser().getDisplayName();
            System.out.println(userID+":"+username);
            //データ追加
            post.setName(username);
            post.setTitle(title);
            post.setDetail(detail);
            post.setLatitude(latitude);
            post.setLongitude(longitude);
            post.setComment(comment);
            if(image==null){
                image="null";
            }
            post.setImage(imageName);
            // user.setOtherComment(othercomment);
            post.setUserId(userID);
            //reff.push().setValue(user);
            maxid=maxid+1;
            reffpost.child(String.valueOf(maxid+1)).setValue(post);
            Toast.makeText(MainActivity.this,"投稿されました！",Toast.LENGTH_LONG).show();
           // Intent intent =new Intent(MainActivity.this,MyPostMapsActivity.class);
            //intent.putExtra("maxId",maxid);
            //startActivity(intent);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr =getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    //画像をStorageに入れる
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
                    Toast.makeText(MainActivity.this,"画像のアップロードに成功しました",Toast.LENGTH_SHORT).show();
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
    //ギャラリーアイコンをクリックしたとき
    public void onGyarallyClicked(View view){
        imageView=findViewById(R.id.ivGallery);
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_GALLERY);
        //openFileChooser();
    }
    //カメラアイコンをクリックしたとき
    public void onCameraImageClick(View view){
        //WRITE_EXTERNAL_STORAGEの許可が下りていないなら…
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //WRITE_EXTERNAL_STORAGEの許可を求めるダイアログを表示。その際、リクエストコードを2000に設定。
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        //日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタを生成。
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //現在の日時を取得。
        Date now = new Date(System.currentTimeMillis());
        //取得した日時データを「yyyyMMddHHmmss」形式に整形した文字列を生成。
        String nowStr = dateFormat.format(now);
        //ストレージに格納する画像のファイル名を生成。ファイル名の一意を確保するためにタイムスタンプの値を利用。
        String fileName = "UseCameraActivityPhoto_" + nowStr +".jpg";

        //ContentValuesオブジェクトを生成。
        ContentValues values = new ContentValues();
        //画像ファイル名を設定。
        values.put(MediaStore.Images.Media.TITLE, fileName);
        //画像ファイルの種類を設定。
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        //ContentResolverオブジェクトを生成。
        ContentResolver resolver = getContentResolver();
        //ContentResolverを使ってURIオブジェクトを生成。
        mImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Intentオブジェクトを生成。
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Extra情報として_imageUriを設定。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent,RESULT_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       //カメラアプリからの戻りでかつ撮影成功の場合
        if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK) {
            //mImageUri=null;
            //Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            /*if(data!=null){
                mImageUri=data.getData();
            }*/
            imageView=findViewById(R.id.ivCamera);
            imageView.setImageURI(mImageUri);
            //imageView.setImageBitmap(bitmap);
            //imageByte=imageViewToByte(ivCamera);
        }
        //ギャラリー
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
                //imageByte=imageViewToByte(imageView);
            } catch (Exception e) {

            }
            // ContentResolver経由でファイルパスを取得
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

    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    public void onMapShowCurrentButtonClick(View view){

    }
    public void onInputClicked(View view){//セットボタンクリック時に緯度経度を入力した数値で表示
        EditText etLatitude=findViewById(R.id.etSetLatitude);
        EditText etLongitude=findViewById(R.id.etSetLongitude);
        TextView tvLatitude=findViewById(R.id.tvLatitude);
        TextView tvLongitude=findViewById(R.id.tvLongitude);
        String setLatitude=etLatitude.getText().toString();
        String setLongitude=etLongitude.getText().toString();
        tvLatitude.setText(setLatitude);
        tvLongitude.setText(setLongitude);
    }
    public void onSearchClicked(View view){

    }

    public void onPresentClicked(View view){

    }
    public void onClearButtonClicked(View view){

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

    //現在の日時を返す
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
        switch (itemId){
            case R.id.menuSeeMyMap:
                Intent mypostIntent=new Intent(MainActivity.this,MyPostMapsActivity.class);
                //Intent mypostIntent=new Intent(MainActivity.this,Information.class);
                startActivity(mypostIntent);
                break;
            case R.id.menuInformation:
                //Intent informationIntent=new Intent(MainActivity.this,Information.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
   }




/*
サインイン・サインアップ処理参考
https://www.youtube.com/watch?v=tbh9YaWPKKs

https://www.youtube.com/watch?v=eGWu0-0TWFI
* */
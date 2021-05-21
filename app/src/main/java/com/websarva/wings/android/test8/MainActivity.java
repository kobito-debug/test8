package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private String title="";
    RadioGroup rgSelect;
    public String username="taro";
    User user;
    long maxid=0;
    DatabaseReference reff;

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

        user=new User();
        reff=FirebaseDatabase.getInstance().getReference().child("User");
        reff.addValueEventListener(new ValueEventListener() {
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

        Button btPost = findViewById(R.id.btPost);
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostClicked(view);
            }
        });

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
        String sUserId= UUID.randomUUID().toString();
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
        Double image=0.0;
        String othercomment="";

        user.setName(username);
        user.setTitle(title);
        user.setDetail(detail);
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setComment(comment);
        user.setImage(image);
       // user.setOtherComment(othercomment);
        user.setUserId(sUserId);
        //reff.push().setValue(user);
        reff.child(String.valueOf(maxid+1)).setValue(user);
        Toast.makeText(MainActivity.this,"データベース登録完了",Toast.LENGTH_LONG).show();
        Intent intent =new Intent(MainActivity.this,MyPostMapsActivity.class);
        intent.putExtra("maxId",maxid);
        startActivity(intent);
    }

    private void insertData(SQLiteDatabase db, String title, String Detail, byte[] image, Double Latitude, Double Longitude, String Comment, String date, String time){

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
        //[テスト]データ取得処理
        reff=FirebaseDatabase.getInstance().getReference("User");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    user=data.getValue(User.class);
                    String a=user.getDetail();
                    String b=user.getuserId();
                    System.out.println(b+":"+a);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                //データ取得失敗
            }
        });
    }
   }

/*
サインイン・サインアップ処理参考
https://www.youtube.com/watch?v=tbh9YaWPKKs

https://www.youtube.com/watch?v=eGWu0-0TWFI
* */
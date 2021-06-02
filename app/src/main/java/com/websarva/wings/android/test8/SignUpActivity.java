package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText etUsername,etPassword,etDisplayname;
    private FirebaseAuth mAuth;
    ProgressBar pbSignup;
    User user;
    DatabaseReference reffuser;
    long maxid=0;
    String userID,username,classes,displayname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent=getIntent();
        etUsername=findViewById(R.id.etUpUsername);
        etPassword=findViewById(R.id.etUpPassword);
        pbSignup=findViewById(R.id.pbSignup);
        etDisplayname=findViewById(R.id.etDisplayname);
        Spinner spinner=findViewById(R.id.sp);
        classes= spinner.getSelectedItem().toString();
        mAuth=FirebaseAuth.getInstance();
        System.out.println(userID+":"+displayname);
        //Userテーブル
        user=new User();
        reffuser= FirebaseDatabase.getInstance().getReference().child("User");
        reffuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid=snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        Button btSignUp=findViewById(R.id.btSignUp2);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        Button btBack=findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });
        mAuth=FirebaseAuth.getInstance();

    }

    //ユーザー登録
    public void registerUser(){
        username=etUsername.getText().toString().trim();
        String password=etPassword.getText().toString().trim();
        if (username.isEmpty()) {
            etUsername.setError("Emailを入力してください");
            etUsername.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            etUsername.setError("有効なメールアドレスを入力してください");
            etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("パスワードを入力してください");
            etPassword.requestFocus();
            return;
        }
        if(password.length()<6){
            etPassword.setError("パスワードの文字数は6文字以上にしてください");
            etPassword.requestFocus();
            return;
        }

        pbSignup.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                pbSignup.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    saveUserInformation();
                    userID=mAuth.getCurrentUser().getUid();
                    //displayname=etDisplayname.getText().toString();
                    user.setUserId(userID);
                    user.setDisplayname(displayname);
                    user.setClasses(classes);
                    reffuser.child(String.valueOf(maxid+1)).setValue(user);
                   startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"すでにユーザー登録されています",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void saveUserInformation() {
        displayname = etDisplayname.getText().toString();
        if (displayname.isEmpty()) {
            etDisplayname.setError("ユーザー名を入力してください");
            etDisplayname.requestFocus();
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayname).build();
            currentUser.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "プロフィールが更新されました", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}

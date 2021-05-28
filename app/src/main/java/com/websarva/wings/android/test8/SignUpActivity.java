package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText etUsername,etPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent=getIntent();
        etUsername=findViewById(R.id.etUpUsername);
        etPassword=findViewById(R.id.etUpPassword);
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
    public void registerUser(){
        String username=etUsername.getText().toString().trim();
        String password=etPassword.getText().toString().trim();
        if (username.isEmpty()) {
            etUsername.setError("Email is required");
            etUsername.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            etUsername.setError("Please enter a valid email");
            etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if(password.length()<6){
            etPassword.setError("Minimum length of pasword should be 6");
            etPassword.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"ユーザー登録が完了しました",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
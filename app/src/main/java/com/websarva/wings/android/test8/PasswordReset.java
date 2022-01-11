package com.websarva.wings.android.test8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class PasswordReset extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btReset;
    private ProgressBar pbReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        mAuth=FirebaseAuth.getInstance();
        btReset=findViewById(R.id.btreset);
        pbReset=findViewById(R.id.pbReset);
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
        Button btBack=findViewById(R.id.btBackLogin);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PasswordReset.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void resetPassword(){
        EditText etMail=findViewById(R.id.etresetmail);
        String email=etMail.getText().toString().trim();
        if(email.isEmpty()){
            etMail.setError("Email is required!");
            etMail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etMail.setError("Please provide valid email");
            etMail.requestFocus();
            return;
        }
        pbReset.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PasswordReset.this,"メールを確認し、パスワードを再設定してください",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PasswordReset.this,"もう一度、お試しください",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
package com.example.mybus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mybus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    EditText email, pwd;
    String s_email = "", s_pwd = "";
    Button signin;
    FirebaseAuth auth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_txt_2);
        pwd = findViewById(R.id.pwd_txt_2);
        signin = findViewById(R.id.signin_btn_2);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifydetails()){
                    signinUser(s_email, s_pwd);
                }
            }
        });
    }

    private void signinUser(String email, String pwd) {

        pd = new ProgressDialog(SignIn.this);
        pd.setMessage("Signing In...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            pd.dismiss();
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean verifydetails() {
        s_email = email.getText().toString().trim();
        s_pwd = pwd.getText().toString().trim();

        if (s_email.length() == 0 || s_email.equals("") || s_email == null) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (s_pwd.length() == 0 || s_pwd.equals("") || s_pwd == null) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
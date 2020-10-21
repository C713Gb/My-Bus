package com.example.mybus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mybus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "HELLO1";
    EditText email, pwd;
    String s_email = "", s_pwd = "", s_profile = "";
    Button signup;
    FirebaseAuth auth;
    DatabaseReference reference;
    Spinner profile;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_txt);
        pwd = findViewById(R.id.pwd_txt);
        signup = findViewById(R.id.signup_btn_2);
        profile = findViewById(R.id.profile_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.profile_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        profile.setAdapter(adapter);
        profile.setOnItemSelectedListener(this);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyDetails()){
                    registerUser(s_email, s_pwd, s_profile);
                }
            }
        });

    }

    private void registerUser(String email, String pwd, String profile) {

        pd = new ProgressDialog(SignUp.this);
        pd.setMessage("Signing Up...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            try {

                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String id = firebaseUser.getUid();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", id);
                                hashMap.put("email", email);
                                hashMap.put("profile", profile);

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pd.dismiss();
                                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            pd.dismiss();
                                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                pd.dismiss();
                                Log.d(TAG, e.getMessage());
                            }


                        } else {
                            pd.dismiss();
                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean verifyDetails() {
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

        if (s_pwd.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (s_profile.length() == 0 || s_profile.equals("") || s_profile == null) {
            Toast.makeText(this, "Select Profile", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        s_profile = parent.getItemAtPosition(position).toString().trim();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
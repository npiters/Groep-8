package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;

public class MainActivity extends AppCompatActivity {

    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;
    private TextView eAttemptinfo;
    private TextView eRegister;
    private FirebaseAuth fAuth;

    private String adminEmail = "Admin";
    private String adminPassword = "Test1";

    boolean isValid = false;
    private int tries = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eEmail = findViewById(R.id.emailLogin);
        ePassword = findViewById(R.id.passwordLogin);
        eLogin = findViewById(R.id.buttonLogin);
        eAttemptinfo = findViewById(R.id.attemptInfo);
        eRegister = findViewById(R.id.register);
        fAuth = FirebaseAuth.getInstance();

        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = eEmail.getText().toString();
                String inputPassword = ePassword.getText().toString();

                if(inputEmail.isEmpty() || inputPassword.isEmpty()){
                    Toast.makeText(MainActivity.this, "Vul alle gegevens correct in!", Toast.LENGTH_LONG).show();
                }
                else{
                    isValid = Validate(inputEmail, inputPassword);
                    if(!isValid){
                        tries--;
                        Toast.makeText(MainActivity.this, "Email of wachtwoord is incorrect.", Toast.LENGTH_LONG).show();
                        eAttemptinfo.setText("Aantal pogingen resterend: " + tries);
                        if(tries == 0){
                            eLogin.setEnabled(false);
                            Toast.makeText(MainActivity.this, "Inlog pogingen overschreden, herstart de applicatie.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        fAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Inloggen succesvol.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this, Profielkeuze.class));
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //Intent profileChoise = new Intent(MainActivity.this, Profielkeuze.class);
                        //startActivity(profileChoise);
                    }
                }
            }
        });

        eRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(MainActivity.this, Registreren.class);
                startActivity(register);
            }
        });
    }

    private boolean checkAdmin(String email, String password){
        if(email.equals(adminEmail) && password.equals(adminPassword)){
            return true;
        }
        return false;
    }

    private boolean Validate(String Email, String Password) {
        boolean Valid = false;
        if (Email.isEmpty()) {
            eEmail.setError("Emailadres is vereist.");
        }
        else if (Password.isEmpty()) {
            ePassword.setError("Wachtwoord is vereist");
        }
        else{
            Valid = true;
        }
        return Valid;
    }
}
package com.example.login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import java.util.Calendar;

public class Registreren extends AppCompatActivity {

    ImageView profilePicture;
    TextView DateOfBirth;
    DatePickerDialog.OnDateSetListener setListener;

    //User Information
    EditText firstName,surName,streetName,postalCode,houseNumber,bankAccount,emailAddress,passWord,passWordRepeat;
    Button createAccount;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registreren);

        firstName = findViewById(R.id.FirstName);
        surName = findViewById(R.id.SurName);
        streetName = findViewById(R.id.StreetName);
        postalCode = findViewById(R.id.PostalCode);
        houseNumber = findViewById(R.id.HouseNumber);
        bankAccount = findViewById(R.id.BankAccount);
        emailAddress = findViewById(R.id.EmailAddress);
        passWord = findViewById(R.id.NewPassword);
        passWordRepeat = findViewById(R.id.NewPasswordRepeat);
        createAccount = findViewById(R.id.SaveProfile);
        DateOfBirth = findViewById(R.id.Date);
        profilePicture = findViewById(R.id.ProfilePicture);
        fAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(Registreren.this, MainActivity.class));
            finish();
        }

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regFirstname = firstName.getText().toString();
                String regSurname = surName.getText().toString();
                String regStreetname = streetName.getText().toString();
                String regPostalcode = postalCode.getText().toString();
                String regHousenumber = houseNumber.getText().toString();
                String regBankaccount = bankAccount.getText().toString();
                String regEmailaddress = emailAddress.getText().toString();
                String regPassword = passWord.getText().toString();
                String regPasswordrepeat = passWordRepeat.getText().toString();

                if(validate(regFirstname, regSurname, regStreetname, regPostalcode, regHousenumber, regBankaccount, regEmailaddress, regPassword, regPasswordrepeat)){
                    //Upload data to database
                    fAuth.createUserWithEmailAndPassword(regEmailaddress, regPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(Registreren.this, "Uw account is aangemaakt.", Toast.LENGTH_LONG).show();
                               startActivity(new Intent(Registreren.this, MainActivity.class));
                           }
                           else{
                               Toast.makeText(Registreren.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }
            }
        });

        DateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                      Registreren.this, android.R.style.Theme_Holo_Dialog_MinWidth,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                DateOfBirth.setText(date);
            }
        };

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                profilePicture.setImageURI(imageUri);
            }
        }
    }

    private boolean validate(String firstname, String surname, String streetname, String postalcode, String housenumber, String bankaccount, String emailaddress, String password, String passwordrepeat){
        Boolean result = false;
        if(firstname.isEmpty() || surname.isEmpty()){
            Toast.makeText(Registreren.this, "Naam is niet correct ingevuld.", Toast.LENGTH_LONG).show();
        }
        else if(streetname.isEmpty() || postalcode.isEmpty() || housenumber.isEmpty()){
            Toast.makeText(Registreren.this, "Adres gegevens zijn niet correct ingevuld.", Toast.LENGTH_LONG).show();
        }
        else if(bankaccount.isEmpty() || emailaddress.isEmpty()){
            Toast.makeText(Registreren.this, "Emailadres of bankaccount is niet ingevuld.", Toast.LENGTH_LONG).show();
        }
        else if(!passwordrepeat.equals(password)){
            Toast.makeText(Registreren.this, "Wachtwoorden komen niet overeen.", Toast.LENGTH_LONG).show();
        }
        else{
            result = true;
        }
        return result;
    }
}
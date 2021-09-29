package com.group_7.mhd.mohammed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^"+
                    "(?=.*[a-z])" +     //at least one lowercase
                    "(?=.*[A-Z])" +     //at least one upercase
                    "(?=.*[0-9])" +     //at least one digit
                    "(?=.*[@#$%^&+=])" +    //at least one special character
                    "(?=\\S+$)" +          //no white space
                    ".{6,}" +               //at least six digit
                    "$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile(new String ("^[a-zA-Z\\s]*$"));

    EditText edit_Phone, edit_Name, edit_Password, edit_Password_repeat, edit_SecureCode;
    Button btnSignUp,txtsignin;
    TextView txtlang;

    AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edit_Phone = findViewById(R.id.edit_Phone);
        edit_Name = findViewById(R.id.edit_Name);
        edit_Password = findViewById(R.id.edit_Password);
        edit_Password_repeat = findViewById(R.id.edit_Password_repeat);
        edit_SecureCode = findViewById(R.id.edit_SecureCode);
        txtsignin = findViewById(R.id.txtSignin);
        txtlang = findViewById(R.id.txtLanguage);

        btnSignUp = findViewById(R.id.btn_signUp);
        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference(Common.USER_TABLE);

        txtlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actisign = new Intent(SignUp.this,SignIn.class);
                startActivity(actisign);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                        mDialog.setMessage("Please wait...");
                        mDialog.show();

                        table_user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int phoneLength = (edit_Phone.getText().toString()).length();
                                String phoneformat = edit_Phone.getText().toString();
                                if (phoneLength==10){
                                    phoneformat = (edit_Phone.getText().toString()).substring(1);
                                }else if (phoneLength==13){
                                    phoneformat = (edit_Phone.getText().toString()).substring(4);
                                }else if (phoneLength==14){
                                    phoneformat = (edit_Phone.getText().toString()).substring(5);
                                }

                                //Check if user phone number already exist
                                if (dataSnapshot.child(phoneformat).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, R.string.phonea, Toast.LENGTH_SHORT).show();

                                } else {
                                    mDialog.dismiss();

                                    User user = new User(edit_Name.getText().toString(),
                                            edit_Password.getText().toString(),
                                            edit_SecureCode.getText().toString(),
                                            phoneformat);
                                    table_user.child(phoneformat).setValue(user);
                                    Toast.makeText(SignUp.this, R.string.signeucc, Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Toast.makeText(SignUp.this, R.string.check, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    private boolean validate() {
        boolean valid = true;
        String phone_validate = edit_Phone.getText().toString().trim();
        String password_validate = edit_Password.getText().toString().trim();
        String repeat_password = edit_Password_repeat.getText().toString().trim();
        String name_validate = edit_Name.getText().toString().trim();
        String secure_validate = edit_SecureCode.getText().toString().trim();

        int phone_length=13;
        if (phone_validate.startsWith("9")){
            phone_length=9;
        }
        if (phone_validate.startsWith("09")){
            phone_length=10;
        }
        if (phone_validate.startsWith("002519")){
            phone_length=14;
        }
        if (phone_validate.isEmpty()||!(phone_validate.startsWith("9")||phone_validate.startsWith("09")||(phone_validate.startsWith("+2519"))||(phone_validate.startsWith("002519")))||!(phone_length==phone_validate.length())) {
            edit_Phone.setError(getString(R.string.err_tel));
            valid = false;
        }
        if (password_validate.isEmpty()||!PASSWORD_PATTERN.matcher(password_validate).matches()){
            edit_Password.setError(getString(R.string.err_password));
            valid = false;
        }
        if (!repeat_password.equals(password_validate)){
            edit_Password_repeat.setError(getString(R.string.err_password_confirmation));
            valid = false;
        }
        if (secure_validate.isEmpty()||!PASSWORD_PATTERN.matcher(password_validate).matches()){
            edit_SecureCode.setError(getString(R.string.err_secure));
            valid = false;
        }
        if (name_validate.isEmpty()||(name_validate.length()>20||name_validate.length()<2)||!NAME_PATTERN.matcher(name_validate).matches()){
            edit_Name.setError(getString(R.string.err_name));
            valid = false;
        }

        return valid;
    }

    //langs
    private void showChangeLanguageDialog() {
        final String[] listItems = {"English","አማርኛ"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SignUp.this);
        builder.setTitle(R.string.choosel);
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en");
                    recreate();
                }
                else if(i==1){
                    setLocale("am");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void setLocale(String langs) {
        Locale locale = new Locale(langs);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",langs);
        editor.apply();
    }
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }
}

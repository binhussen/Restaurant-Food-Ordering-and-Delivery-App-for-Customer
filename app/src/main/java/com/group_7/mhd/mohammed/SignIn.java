package com.group_7.mhd.mohammed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.paperdb.Paper;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignIn extends AppCompatActivity implements Validator.ValidationListener {

    private static final String TAG = "MainActivity";
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^"+
                    "(?=.*[a-z])" +     //at least one lowercase
                    "(?=.*[A-Z])" +     //at least one upercase
                    "(?=.*[0-9])" +     //at least one digit
                    "(?=.*[@#$%^&+=])" +    //at least one special character
                    "(?=\\S+$)" +          //no white space
                    ".{6,}" +               //at least six digit
                    "$");

    EditText editPhone, editPassword;

    Button btnSignIn,txtsignup;
    CheckBox ckbRemember;
    TextView txtForgetPwd,txtlang;

    FirebaseDatabase database;
    DatabaseReference table_user;

    AwesomeValidation awesomeValidation;
    private Validator validator;

    //forget
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        awesomeValidation = new AwesomeValidation(BASIC);

        validator = new Validator(this);
        validator.setValidationListener(this);

        editPhone = findViewById(R.id.edit_Phone);
        editPassword = findViewById(R.id.edit_Password);
        btnSignIn = findViewById(R.id.btn_signIn);
        /*ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);*/
        txtForgetPwd = (TextView) findViewById(R.id.txtForgetPwd);
        txtsignup = (Button) findViewById(R.id.txtSignUp);
        txtlang = (TextView) findViewById(R.id.txtLanguage);

        /*awesomeValidation.addValidation(SignIn.this, R.id.edit_Phone, RegexTemplate.TELEPHONE, R.string.err_tel);*/

        //int paper
        Paper.init(this);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference(Common.USER_TABLE);

        txtForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetPwdDialog();
            }
        });

        txtsignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent actisign = new Intent(SignIn.this,SignUp.class);
                startActivity(actisign);
            }
        });

        txtlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*awesomeValidation.validate();*/
                if (validate())
                {
                    signInUser(editPhone.getText().toString(),editPassword.getText().toString());
                }
            }
        });
    }

    private void signInUser(final String userphone, final String userpwd) {
        if (Common.isConnectedToInternet(getBaseContext())) {
            //save user and password

            final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();

            table_user.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int phoneLength = userphone.length();
                    String phoneformat = userphone;
                    if (phoneLength==10){
                        phoneformat = userphone.substring(1);
                    }else if (phoneLength==13){
                        phoneformat = userphone.substring(4);
                    }else if (phoneLength==14){
                        phoneformat = userphone.substring(5);
                    }
                    //Check if user not exist in Database
                            /*if (ckbRemember.isChecked())
                            {*/
                    /*}*/

                    if (dataSnapshot.child(phoneformat).exists()) {
                        //Get user information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phoneformat).getValue(User.class);
                        user.setPhone(phoneformat);//set phone
                        if (user.getPassword().equals(userpwd)) {

                            Paper.book().write(Common.USER_KEY,phoneformat);
                            Paper.book().write(Common.PWD_KEY,userpwd);

                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, R.string.signsuccess, Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(SignIn.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                            //delete lisnr=ed data
                            table_user.removeEventListener(this);

                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, R.string.wropass, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, R.string.userd, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(SignIn.this, R.string.check, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void showForgetPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forget Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forget_view = inflater.inflate(R.layout.forget_password_layout,null);

        builder.setView(forget_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText editPhone = (MaterialEditText) forget_view.findViewById(R.id.edit_Phone);
        final MaterialEditText editSecureCode = (MaterialEditText) forget_view.findViewById(R.id.edit_SecureCode);


        //create Dialog and show
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Get AlertDialog from dialog
        final AlertDialog diagview = ((AlertDialog) dialog);
        Button ok = (Button) diagview.findViewById(R.id.ok);
        Button cancel = (Button) diagview.findViewById(R.id.cancel);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatee()){
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dialog.dismiss();

                            int phoneLength = editPhone.getText().toString().length();
                            String phoneformat = editPhone.getText().toString();
                            if (phoneLength==10){
                                phoneformat = editPhone.getText().toString().substring(1);
                            }else if (phoneLength==13){
                                phoneformat = editPhone.getText().toString().substring(4);
                            }else if (phoneLength==14){
                                phoneformat = editPhone.getText().toString().substring(5);
                            }

                            User user = dataSnapshot.child(phoneformat).getValue(User.class);
                            if (user.getSecureCode().equals(editSecureCode.getText().toString()))
                                Toast.makeText(SignIn.this, "Your Password : "+user.getPassword(),Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignIn.this,"Wrong Secuer code!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                        }
                    });
                }
            }

            private boolean validatee() {
                boolean valid = true;
                String phone_validate = editPhone.getText().toString().trim();
                String password_validate = editSecureCode.getText().toString().trim();

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
                    editPhone.setError(getString(R.string.err_tel));
                    valid = false;
                }

                if (password_validate.isEmpty()||!PASSWORD_PATTERN.matcher(password_validate).matches()){
                    editSecureCode.setError(getString(R.string.err_password));
                    valid = false;
                }
        /*if (password_validate.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(password_validate).matches()){

        }*/

                return valid;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    //langs
    private void showChangeLanguageDialog() {
        final String[] listItems = {"English","አማርኛ"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SignIn.this);
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

    private boolean validate() {

        boolean valid = true;
        String phone_validate = editPhone.getText().toString().trim();
        String password_validate = editPassword.getText().toString().trim();

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
            editPhone.setError(getString(R.string.err_tel));
            valid = false;
        }

        if (password_validate.isEmpty()||!PASSWORD_PATTERN.matcher(password_validate).matches()){
            editPassword.setError(getString(R.string.err_password));
            valid = false;
        }
        /*if (password_validate.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(password_validate).matches()){

        }*/

        return valid;
    }
    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }
}

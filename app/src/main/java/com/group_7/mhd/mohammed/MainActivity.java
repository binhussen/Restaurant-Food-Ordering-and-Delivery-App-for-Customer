package com.group_7.mhd.mohammed;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.Toast;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.group_7.mhd.mohammed.Common.Common;
        import com.group_7.mhd.mohammed.Model.User;

        import io.netopen.hotbitmapgg.library.view.RingProgressBar;
        import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    //setting up a finite time for screen delay
    private static int splash_time_Out=2000;
    private Context context;

    RingProgressBar ringProgressBar;
    int progress=0;
    Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0)
            {
                if(progress<100)
                {
                    progress+=5;
                    ringProgressBar.setProgress(progress);
                }
            }
        }
    };

    SharedPreferences sharedPreferences;
    Boolean save_login,skip;
    SharedPreferences.Editor edit;
    String type,name,resname;


    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference(Common.USER_TABLE);

        //init paper
        Paper.init(this);

        ringProgressBar = (RingProgressBar) findViewById(R.id.ringProgress);
        ringProgressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                okDone();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(100);
                        myhandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void signInUser(final String userphone, final String userpwd) {
        if (Common.isConnectedToInternet(getBaseContext())) {
            //save user and password

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
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
                            Toast.makeText(MainActivity.this, R.string.signsuccess, Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                            //delete lisnr=ed data
                            table_user.removeEventListener(this);

                        } else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, R.string.wropass, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, R.string.userd, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, R.string.check, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void okDone(){
        if (!isNetworkAvilabe()) {
            //Creating an Alertdialog
            AlertDialog.Builder CheckBuild = new AlertDialog.Builder(MainActivity.this);
            CheckBuild.setIcon(R.drawable.no);
            CheckBuild.setTitle(R.string.error);
            CheckBuild.setMessage(R.string.check);

            //Builder Retry Button

            CheckBuild.setPositiveButton(R.string.skip, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {
                    //creating intent and going to the home activity
                    Intent newintent = new Intent(MainActivity.this, SignIn.class);
                    //starting the activity
                    startActivity(newintent);

//                    Intent intent = new Intent(context, First_Activity.class);
//                    context.startActivity(intent);
                    //when intent is start and go to home class then main activity will finish
                    finish();
                }

            });
            /*CheckBuild.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {
                    //Restart The Activity
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            });*/
            CheckBuild.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //Exit The Activity
                    finish();
                }

            });
            AlertDialog alertDialog = CheckBuild.create();
            alertDialog.show();
        } else {
            //login automatcally
            String user = Paper.book().read(Common.USER_KEY);
            String pwd = Paper.book().read(Common.PWD_KEY);
            if (user != null && pwd != null){
                if (!user.isEmpty() && !pwd.isEmpty()){
                    signInUser(user,pwd);
                }
            }else {
                //creating intent and going to the home activity
                Intent newintent = new Intent(MainActivity.this, SignIn.class);
                //starting the activity
                startActivity(newintent);
                //when intent is start and go to home class then main activity will finish
                finish();
            }
        }
    }

    private boolean isNetworkAvilabe() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
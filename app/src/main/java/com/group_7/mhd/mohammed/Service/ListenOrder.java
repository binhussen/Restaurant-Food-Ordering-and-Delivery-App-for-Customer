/*
package com.group_7.mhd.mohammed.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Model.Request;
import com.group_7.mhd.mohammed.OrderStatus;
import com.group_7.mhd.mohammed.R;

public class ListenOrder extends Service implements ChildEventListener{

    FirebaseDatabase db;
    DatabaseReference requests;

    public ListenOrder(){
    }
    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    public void onCreate(){
        super.onCreate();
        db=db.getInstance();
        requests=db.getReference("Requests");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        */
/*Request request = dataSnapshot.getValue(Request.class);
        assert request != null;
        if(request.getStatus().equals("0") && request.getPhone().equals(Common.currentUser.getPhone()))
            showNotification(dataSnapshot.getKey(), request);*//*

    }

    @Override
    public void onChildChanged( DataSnapshot dataSnapshot,  String s) {

        Request requests=dataSnapshot.getValue(Request.class);
        showNotification(dataSnapshot.getKey(),requests);
    }

    private void showNotification(String key, Request requests) {

        Intent intent= new Intent(getBaseContext(), OrderStatus.class);
        intent.putExtra("userPhone",requests.getPhone());
        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Mohammed")
                .setContentInfo("Your order was updated")
                .setContentText("Order #"+key+" was Update status to "+Common.convertCodeToStatus(requests.getStatus()))
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager= (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }

    @Override
    public void onChildRemoved( DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved( DataSnapshot dataSnapshot,  String s) {

    }

    @Override
    public void onCancelled( DatabaseError databaseError) {

    }

}
*/

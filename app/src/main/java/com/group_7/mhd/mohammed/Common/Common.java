package com.group_7.mhd.mohammed.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.group_7.mhd.mohammed.Model.User;
import com.group_7.mhd.mohammed.Remote.APIService;
import com.group_7.mhd.mohammed.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class Common {
    public static final String SHIPPERS_TABLE = "Drivers";
    public static final String ORDER_NEED_SHIPPERS_TABLE = "Delivering";
    public static final String SHIPPERS_INFO_TABLE = "Delivered";
    public static final String ORDER_TABLE = "Order";
    public static final String CATEGORY_TABLE = "Category";
    public static final String CHAFS_TABLE = "Chafs";
    public static final String COMMENT_TABLE = "Comment";
    public static final String FOOD_TABLE = "Foods";
    public static final String TOKEN_TABLE = "Tokens";
    public static final String USER_TABLE = "User";

    public static final int REQUEST_CODE =1000;

    public static User currentUser;

    public static String PHONE_TEXT = "userPhone";


    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    //public static Request currentRequest;
    public final static String UPDATE = "Update";
    public final static String DELETE = "Delete";
    public final static String USER_PHONE = "UserPhone";
    public final static String USER_PASSWORD = "UserPassword";
    public final static String USER_NAME = "UserName";
    public final static String CLIENT = "client";
    public final static String SERVER = "server";

    public final static String USER_KEY = "USER_KEY";
    public final static String PWD_KEY = "PWD_KEY";

    public static double DELIVER = 0;

    public static String convertCodeToStatus(String status) {
        if (status.equals("0")) {
            return "Placed";
        } else if (status.equals("1")) {
            return "Payed";
        } else if (status.equals("2")) {
            return "Cooked";
        } else if (status.equals("3")) {
            return "Delivering";
        } else if (status.equals("4")){
            return "Delivered";
        } else{
            return "Cash On Delivery";
        }
    }
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
            /*assert connectivityManager != null;
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();*/
        }
        return false;
    }
    public static String getDate(Long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy-HH:mm"
                        ,calendar)
                        .toString());
        return date.toString();
    }
    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%.2f",d);
    }
}

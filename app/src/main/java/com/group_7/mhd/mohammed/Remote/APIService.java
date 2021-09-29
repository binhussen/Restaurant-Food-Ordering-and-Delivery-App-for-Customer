package com.group_7.mhd.mohammed.Remote;

import com.group_7.mhd.mohammed.Model.DataMessage;
import com.group_7.mhd.mohammed.Model.MyResponse;
import com.group_7.mhd.mohammed.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                "Content-Type:application/json",
                "Authorization:key=AAAAZIHvH9g:APA91bE5pdfbZ8NXxEJEgLqoW0Sqg0SMeAtA6OpB0Xtssdhaefc2uArvIHx9PwSxJN0Am4MZqPYepyO2V-boSdzO_dlPUc5cnt2ytD3suw67JOVzPzENjeFLGkeOOClnI8YmfyOo4vEY"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}

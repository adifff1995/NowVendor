package com.app.radarvendor.Utils;//package com.app.radar.Utils;

import android.content.Intent;
import android.util.Log;

import com.app.radarvendor.Activities.MainActivity;
import com.app.radarvendor.Activities.MainDeliveryActivity;
import com.app.radarvendor.Activities.MainServicesActivity;
import com.app.radarvendor.Module.OrderFromUser;
import com.app.radarvendor.Module.OrderResponse;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;


import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String ORDER_NUM = "order_num";

    private String title;
    private String desc;
    private int id;

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        Hawk.put(Constant.fcm, token);
        Log.d(TAG, "onNewToken: " + token);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        GlobalMethods.printGson("onMessageReceived", remoteMessage);
        GlobalMethods.printGson("onMessageData", remoteMessage.getData());


        if (remoteMessage.getNotification() != null) {


//            String order_num = remoteMessage.getData().get(ORDER_NUM);
//            Log.e("emoteMessage.getDat: ", order_num + "");
//            if (MainActivity.isInstance()) {
//                MainActivity.getInstance().getNotificationFromFcm(order_num);
//            }
            title = remoteMessage.getNotification().getTitle();
            desc = remoteMessage.getNotification().getBody();
            Gson gson = new Gson();
            sendNotification(remoteMessage);


        }


    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Gson gson = new Gson();
        String type = remoteMessage.getData().get("type");
        String order_id = remoteMessage.getData().get("id");
        JSONObject object = new JSONObject(remoteMessage.getData());
        OrderResponse orderResponse = null;
        OrderFromUser orderFromUser = null;
        assert type != null;
        if (type.equals("7"))
            orderResponse = gson.fromJson(object.toString(), OrderResponse.class);
        else if (type.equals("9"))
            orderFromUser = gson.fromJson(object.toString(), OrderFromUser.class);

        Intent intent = null;
        MyNotificationManager manager = new MyNotificationManager(this);
        Log.e("sendNotification: ", type + " " + order_id);
//        assert type != null;
//        if (type.equals("7"))
//            EventBus.getDefault().post(new MessageEvent(Integer.parseInt(order_id), orderResponse));


        switch (type) {
            case "1":
                intent = new Intent(getApplicationContext(), MainServicesActivity.class);
                intent.putExtra("type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                manager.showSmallNotification(id, title, desc, intent);
            case "2":
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                manager.showSmallNotification(id, title, desc, intent);
            case "5":
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                manager.showSmallNotification(id, title, desc, intent);
            case "6":
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                manager.showSmallNotification(id, title, desc, intent);
                break;
            case "7":
                intent = new Intent(getApplicationContext(), MainDeliveryActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("order", orderResponse);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                manager.showSmallNotification(id, title, desc, intent);
                startActivity(intent);
                break;
            case "9":
                intent = new Intent(getApplicationContext(), MainDeliveryActivity.class);
                intent.putExtra("type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                manager.showSmallNotification(id, title, desc, intent);
                startActivity(intent);
                break;
        }


    }
}

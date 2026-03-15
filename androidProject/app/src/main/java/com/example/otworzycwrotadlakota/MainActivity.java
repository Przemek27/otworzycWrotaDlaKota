package com.example.otworzycwrotadlakota;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String espUrl = "http://192.168.1.105"; //fixme: user should be able to set IP
    OkHttpClient client = new OkHttpClient();
    Handler handler = new Handler();
    int interval = 1000; // 1 second

    TextView textResult;
    int previousSensorState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }

        Button buttonSensor = findViewById(R.id.buttonSensor);
        Button buttonLedOn = findViewById(R.id.buttonLedOn);
        Button buttonLedOff = findViewById(R.id.buttonLedOff);

        textResult = findViewById(R.id.textResult);

        buttonSensor.setOnClickListener(v ->
                sendRequest("/sensor"));

        buttonLedOn.setOnClickListener(v ->
                sendRequest("/led/on"));

        buttonLedOff.setOnClickListener(v ->
                sendRequest("/led/off"));

        handler.post(sensorPoller);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelId = "sensor_channel";

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel =
                    new NotificationChannel(channelId, "Sensor Alerts",
                            NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sensorPoller);
    }

    void sendRequest(String endpoint) {

        Request request = new Request.Builder()
                .url(espUrl + endpoint)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(() ->
                        textResult.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                int sensorState = result.contains("1") ? 1 : 0;

                runOnUiThread(() -> {

                    textResult.setText("Sensor: " + sensorState);

                    if(previousSensorState == 0 && sensorState == 1){
                        showNotification();
                    }

                    previousSensorState = sensorState;
                });
            }
        });
    }

    Runnable sensorPoller = new Runnable() {
        @Override
        public void run() {
            sendRequest("/sensor");
            handler.postDelayed(this, interval);
        }
    };

    void showNotification(){

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this, "sensor_channel")
                .setContentTitle("Cat Gate Alert")
                .setContentText("Sensor triggered!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        manager.notify(1, notification);
    }
}
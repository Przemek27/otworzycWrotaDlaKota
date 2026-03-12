package com.example.otworzycwrotadlakota;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String espUrl = "http://192.168.1.102";
    OkHttpClient client = new OkHttpClient();

    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                runOnUiThread(() ->
                        textResult.setText(result));
            }
        });
    }
}
package com.example.android.serverapp;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.serverapp.BluetoothConnection.ManageConnectThread;
import com.example.android.serverapp.BluetoothConnection.ServerConnectThread;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                ServerConnectThread serverConnectThread = new ServerConnectThread();
                serverConnectThread.acceptConnect(bluetoothAdapter, UUID.fromString("51fd4c3c-6578-4de7-bf45-e51fabc2366c"));

                // wait for client to initialize the connection
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ManageConnectThread manageConnectThread = new ManageConnectThread();

                try {
                    manageConnectThread.sendData(serverConnectThread.getbTSocket(), 4); // 4 items thrown
                } catch (IOException e) {
                    e.printStackTrace();
                }

                serverConnectThread.closeConnect();
            }
        });


    }
}

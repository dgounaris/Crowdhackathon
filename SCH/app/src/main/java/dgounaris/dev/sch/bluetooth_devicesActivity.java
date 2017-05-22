package dgounaris.dev.sch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import dgounaris.dev.sch.adapter.Bluetooth_devicesAdapter;

import static android.R.attr.data;

public class bluetooth_devicesActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, 0);
        } else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


            ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();


            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    devices.add(device);
                }

                Bluetooth_devicesAdapter bluetoothDeviceArrayAdapter = new Bluetooth_devicesAdapter(this, devices);

                ListView listView = (ListView) findViewById(R.id.bluetooth_list);
                listView.setAdapter(bluetoothDeviceArrayAdapter);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish(); // RETURN TO CONNECTED_FRAGMENT
                    }
                }, 3000);
                // HERE WILL BE ADDED ON CLICK LISTENER FOR LIST ITEM IN ORDER TO RETURN TO CONNECTED_FRAGMENT
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "BlueTooth is now Enabled", Toast.LENGTH_LONG).show();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


                ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();


                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {

                        devices.add(device);
                    }

                    Bluetooth_devicesAdapter bluetoothDeviceArrayAdapter = new Bluetooth_devicesAdapter(this, devices);

                    ListView listView = (ListView) findViewById(R.id.bluetooth_list);
                    listView.setAdapter(bluetoothDeviceArrayAdapter);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish(); // RETURN TO CONNECTED_FRAGMENT
                        }
                    }, 3000);
                    // HERE WILL BE ADDED ON CLICK LISTENER FOR LIST ITEM IN ORDER TO RETURN TO CONNECTED_FRAGMENT
                }
            }
        }
    }
}

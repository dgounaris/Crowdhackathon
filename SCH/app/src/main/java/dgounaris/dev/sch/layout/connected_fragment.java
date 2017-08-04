package dgounaris.dev.sch.layout;

import android.Manifest;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.adapter.Bluetooth_devicesAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DimitrisLPC on 17/5/2017.
 */

public class connected_fragment extends Fragment {

    private Person activeperson;
    private TextView connection_status;
    private ProgressBar progress_bar;
    private TextView points_text;
    private TextView points_raw_text;
    private ListView bluetooth_list;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    BluetoothDevice device_clicked = null;

    public connected_fragment() {
        // Required empty public constructor
    }

    public static connected_fragment newInstance() {
        connected_fragment fragment = new connected_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeperson = (Person) getArguments().getSerializable("activeperson");

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            if (mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();

            // check for permissions
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), "Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += ContextCompat.checkSelfPermission(getContext(), "Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), "BlueTooth is now Enabled", Toast.LENGTH_LONG).show();

                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                getActivity().registerReceiver(mReceiver, filter);
                mBluetoothAdapter.startDiscovery();

            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
//                ListView listView = (ListView) getActivity().findViewById(R.id.bluetooth_list);

                Bluetooth_devicesAdapter bluetoothDeviceArrayAdapter = new Bluetooth_devicesAdapter(context, devices);
                bluetooth_list.setVisibility(View.VISIBLE);
                bluetooth_list.setAdapter(bluetoothDeviceArrayAdapter);
                bluetooth_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        device_clicked = devices.get(position);
                        ((ViewGroup) bluetooth_list.getParent()).removeView(bluetooth_list);
                        onConnectionSeeking();


                    }
                });


            } else
                Toast.makeText(getContext(), "No device found", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_connected_fragment, container, false);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_circle);
        connection_status = (TextView) view.findViewById(R.id.connection_status);
        points_text = (TextView) view.findViewById(R.id.points_text);
        points_raw_text = (TextView) view.findViewById(R.id.points_rawtext);
        bluetooth_list = (ListView) view.findViewById(R.id.bluetooth_list);


//        onConnectionSeeking();
        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void onConnectionSeeking() {
        connection_status.setText("Connecting...");
        progress_bar.setVisibility(View.VISIBLE);
        points_text.setVisibility(View.INVISIBLE);
        points_raw_text.setVisibility(View.INVISIBLE);
    }

    private void onConnectionEstablished() {
        connection_status.setText("Connection established");
        progress_bar.setVisibility(View.INVISIBLE);
        points_text.setVisibility(View.VISIBLE);
        points_text.setText(activeperson.getPoints() + "");
        points_raw_text.setVisibility(View.VISIBLE);
        Handler myHandler = new Handler();
        myHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        addPoints(10);
                    }
                }, 1000
        );
        myHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        addPoints(10);
                    }
                }, 2000
        );
    }

    private void addPoints(int points_added) {
        ValueAnimator animator = ValueAnimator.ofInt(activeperson.getPoints(), activeperson.getPoints() + points_added);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                points_text.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
        activeperson.setPoints(activeperson.getPoints() + points_added);
        ((MainActivity) getActivity()).onAddPoints(activeperson.getId(), points_added);
    }
}

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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import dgounaris.dev.sch.BluetoothConnection.BluetoothConnectThread;
import dgounaris.dev.sch.BluetoothConnection.ManageConnectThread;
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
    TextView textView;

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

            // check for necessary permissions
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

            // delete textview
            ((ViewGroup) textView.getParent()).removeView(textView);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                connection_status.setVisibility(View.INVISIBLE);
                progress_bar.setVisibility(View.INVISIBLE);

                Bluetooth_devicesAdapter bluetoothDeviceArrayAdapter = new Bluetooth_devicesAdapter(context, devices);
                bluetooth_list.setVisibility(View.VISIBLE);
                bluetooth_list.setAdapter(bluetoothDeviceArrayAdapter);
                bluetooth_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        device_clicked = devices.get(position);
                        ((ViewGroup) bluetooth_list.getParent()).removeView(bluetooth_list);
//                        onConnectionSeeking();

//                        Toast.makeText(getActivity(), "Successfully Connected", Toast.LENGTH_LONG);


                        BluetoothConnectThread bluetoothConnectThread = new BluetoothConnectThread();
                        while (!bluetoothConnectThread.connect(device_clicked, UUID.fromString("51fd4c3c-6578-4de7-bf45-e51fabc2366c"))) // UUID ON SERVER MUST BE THE SAME WITH THIS ONE
                            Toast.makeText(getContext(), "Something went wrong, trying again", Toast.LENGTH_LONG);

                        // wait for server to accept connection
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ManageConnectThread manageConnectThread = new ManageConnectThread();

                        // receive data from bin (number of items thrown)
                        int rcvNum = 0;
                        if (bluetoothConnectThread.getbTSocket().isConnected()) {
                            try {
                                rcvNum = manageConnectThread.receiveData(bluetoothConnectThread.getbTSocket());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // some (or none) items were thrown into the bin
                            // update the points of user
                            addPoints(rcvNum * 10); // 10 points for each item


                        }
                        connection_status.setText(rcvNum + "  items thrown in the recycling bin and " + rcvNum * 10 + " points collected!");
                        connection_status.setVisibility(View.VISIBLE);
                        onConnectionEstablished();
                    }
                });


            }
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

        // show text
        textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText("Scanning for nearby recycling bins...");
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.connected_layout);
        layout.addView(textView, 0);

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
        //connection_status.setText("Connection established");
        progress_bar.setVisibility(View.INVISIBLE);
        points_text.setVisibility(View.VISIBLE);
        points_text.setText(activeperson.getPoints() + "");
        points_raw_text.setVisibility(View.VISIBLE);
//        Handler myHandler = new Handler();
//        myHandler.postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        addPoints(10);
//                    }
//                }, 1000
//        );
//        myHandler.postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        addPoints(10);
//                    }
//                }, 2000
//        );
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

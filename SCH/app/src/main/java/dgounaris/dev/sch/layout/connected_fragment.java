package dgounaris.dev.sch.layout;

import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Set;

import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;

/**
 * Created by DimitrisLPC on 17/5/2017.
 */

public class connected_fragment extends Fragment {

    private Person activeperson;
    private TextView connection_status;
    private ProgressBar progress_bar;
    private TextView points_text;
    private TextView points_raw_text;

    private final static int REQUEST_ENABLE_BT = 1;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };


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

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_connected_fragment, container, false);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_circle);
        connection_status = (TextView) view.findViewById(R.id.connection_status);
        points_text = (TextView) view.findViewById(R.id.points_text);
        points_raw_text = (TextView) view.findViewById(R.id.points_rawtext);
        onConnectionSeeking();
        return view;
    }

    private void onConnectionSeeking() {
        connection_status.setText("Connecting...");
        progress_bar.setVisibility(View.VISIBLE);
        points_text.setVisibility(View.INVISIBLE);
        points_raw_text.setVisibility(View.INVISIBLE);
        Handler myHandler = new Handler();
        myHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        onConnectionEstablished();
                    }
                }, 3000);
        //Bluetooth Start

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
        else {
            mBluetoothAdapter.startDiscovery();
        }

        //Bluetooth End
    }

    private void onConnectionEstablished() {
        connection_status.setText("Connection established");
        progress_bar.setVisibility(View.INVISIBLE);
        points_text.setVisibility(View.VISIBLE);
        points_text.setText(activeperson.getPoints()+"");
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
        ValueAnimator animator = ValueAnimator.ofInt(activeperson.getPoints(), activeperson.getPoints()+points_added);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                points_text.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
        activeperson.setPoints(activeperson.getPoints()+points_added);
        ((MainActivity)getActivity()).onAddPoints(activeperson.getId(), points_added);
    }

    

}

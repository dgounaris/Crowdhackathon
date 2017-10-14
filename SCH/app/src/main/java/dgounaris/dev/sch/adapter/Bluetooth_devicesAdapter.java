package dgounaris.dev.sch.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dgounaris.dev.sch.R;

/**
 * Created by antonis on 22-May-17.
 */

public class Bluetooth_devicesAdapter extends ArrayAdapter<BluetoothDevice> {
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        BluetoothDevice current_device = getItem(position);

        ImageView image = (ImageView) listItemView.findViewById(R.id.image);
        image.setVisibility(View.INVISIBLE);

        TextView desc = (TextView) listItemView.findViewById(R.id.trophy_desc);
        desc.setVisibility(View.INVISIBLE);

        TextView name = (TextView) listItemView.findViewById(R.id.title);
        name.setText(current_device.getName());


        return listItemView;
    }

    public Bluetooth_devicesAdapter(Context context, ArrayList<BluetoothDevice> arrayList) {
        super(context, 0, arrayList);
    }
}

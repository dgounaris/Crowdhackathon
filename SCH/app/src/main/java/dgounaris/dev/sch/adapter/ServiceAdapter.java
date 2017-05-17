package dgounaris.dev.sch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.layout.profile_fragment;

/**
 * Created by Rhogarj on 5/13/2017.
 */

public class ServiceAdapter extends ArrayAdapter<Service> {

    Fragment parentFragment;

    @NonNull
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.service_list_item, parent, false);
        }

        final Service current_service = getItem(position);

        TextView mainname = (TextView) listItemView.findViewById(R.id.service_name);
        mainname.setText(current_service.getName());
        TextView slotstext = (TextView) listItemView.findViewById(R.id.service_slots);
        slotstext.setText("Empty slots: " + current_service.getSlots());
        TextView pointstext = (TextView) listItemView.findViewById(R.id.points_needed);
        pointstext.setText(current_service.getPoints_needed() + " points needed");
        Button activateButton = (Button) listItemView.findViewById(R.id.button_activate);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((profile_fragment)parentFragment).onRedeemPoints(current_service.getId(), current_service.getPoints_needed());
            }
        });

        return listItemView;
    }

    public ServiceAdapter(Context context, ArrayList<Service> arrayList, Fragment parent) {
        super(context, 0, arrayList);
        parentFragment = parent;
    }

    public int[] getCurrentServiceDetails(int pos) {
        int[] array = {getItem(pos).getId(), getItem(pos).getPoints_needed()};
        return array;
    }

}

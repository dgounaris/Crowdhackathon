package dgounaris.dev.sch.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.R;

/**
 * Created by DimitrisLPC on 14/5/2017.
 */

public class HallofFameAdapter extends ArrayAdapter<Person>{

    public View getView(int position, View convertView, final ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.hall_of_fame_item, parent, false);
        }

        TextView place = (TextView) listItemView.findViewById(R.id.hof_place);
        int curpos = position+1;
        place.setText(curpos+"");
        TextView name = (TextView) listItemView.findViewById(R.id.hof_name);
        name.setText(getItem(position).getName() + " " + getItem(position).getSurname());
        TextView points = (TextView) listItemView.findViewById(R.id.hof_points);
        points.setText(getItem(position).getPoints()+"");

        return listItemView;
    }

        public HallofFameAdapter(Context context, ArrayList< Person > arrayList) {
            super(context, 0, arrayList);
        }

}

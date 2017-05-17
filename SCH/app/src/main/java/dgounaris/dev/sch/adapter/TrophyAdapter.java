package dgounaris.dev.sch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dgounaris.dev.sch.DBHelper.MyDBHelper;
import dgounaris.dev.sch.R;
/**
 * Created by antonis on 06-Mar-17.
 */

public class TrophyAdapter extends ArrayAdapter<Trophy> {
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Trophy current_word = getItem(position);

        ImageView image = (ImageView) listItemView.findViewById(R.id.image);
        if (current_word.getImage()!=null)
            image.setImageBitmap(current_word.getImage());
        else
            image.setVisibility(View.GONE);

        MyDBHelper dbHelper = new MyDBHelper(getContext());

        TextView miwok = (TextView) listItemView.findViewById(R.id.title);
        miwok.setText(current_word.getTitle());

        TextView desc = (TextView) listItemView.findViewById(R.id.trophy_desc);
        desc.setText(current_word.getDescription());
        return listItemView;
    }

    public TrophyAdapter(Context context, ArrayList<Trophy> arrayList) {
        super(context, 0, arrayList);
    }
}

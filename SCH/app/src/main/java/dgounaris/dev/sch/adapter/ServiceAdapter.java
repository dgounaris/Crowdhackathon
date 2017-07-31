package dgounaris.dev.sch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.layout.profile_fragment;

/**
 * Created by Rhogarj on 5/13/2017.
 */

public class ServiceAdapter extends ArrayAdapter<Service> {

    Fragment parentFragment;
    Person activePerson; //used to complete transactions

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
        final Button activateButton = (Button) listItemView.findViewById(R.id.button_activate);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.add("personid", activePerson.getId()); rp.add("serviceid", current_service.getId());
                APIHelper.post("/services/redeem", rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        if (statusCode==204) {
                            Toast.makeText(getContext(), "Could not load points from server.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(), "Service redeemed successfully", Toast.LENGTH_SHORT).show();
                        for (int i=0;i<response.length();i++) {
                            try {
                                JSONObject json = response.getJSONObject(i);
                                activePerson.setPoints(json.getInt("p_Points"));
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Could not load points from server.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        if (statusCode>=500) {
                            Toast.makeText(getContext(), "Unable to reach server. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (statusCode==401) {
                            Toast.makeText(getContext(), "Could not process request. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (statusCode>=500) {
                            Toast.makeText(getContext(), "Unable to reach server. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (statusCode==401) {
                            Toast.makeText(getContext(), "Could not process request. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (statusCode>=500) {
                            Toast.makeText(getContext(), "Unable to reach server. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (statusCode==401) {
                            Toast.makeText(getContext(), "Could not process request. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });

        return listItemView;
    }

    public ServiceAdapter(Context context, ArrayList<Service> arrayList, Fragment parent, Person person) {
        super(context, 0, arrayList);
        parentFragment = parent;
        activePerson = person;
    }

}

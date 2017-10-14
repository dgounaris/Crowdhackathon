package dgounaris.dev.sch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import dgounaris.dev.sch.APIHandler.ApiClient;
import dgounaris.dev.sch.APIHandler.ApiInterface;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                rp.add("personid", ((Long)activePerson.getId()).toString()); rp.add("serviceid", ((Long)current_service.getId()).toString());
                ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);
                Call<Integer> pointsCall = apiService.redeemService(activePerson.getId(), current_service.getId());
                pointsCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.code()>=500) {
                            Toast.makeText(getContext(), "Couldn't reach server. Try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (response.code()>=400) {
                            Toast.makeText(getContext(), "Error: Bad input provided", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        activePerson.setPoints(response.body());
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
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

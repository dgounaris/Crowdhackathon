package dgounaris.dev.sch.layout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.HOFActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.adapter.ServiceAdapter;

public class profile_fragment extends Fragment {

    private Person activeperson;

    public profile_fragment() {
        // Required empty public constructor
    }

    public static profile_fragment newInstance() {
        profile_fragment fragment = new profile_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeperson = (Person) getArguments().getSerializable("activeperson");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_fragment, container, false);

        //SET NAME, BALANCE
        ImageView profileimg = (ImageView) view.findViewById(R.id.profile_img);
        profileimg.setImageBitmap(activeperson.getmImage().getImage());
        TextView nameText = (TextView) view.findViewById(R.id.name);
        nameText.setText(activeperson.getName() + " " + activeperson.getSurname());
        TextView balance = (TextView) view.findViewById(R.id.balance);
        balance.setText(activeperson.getPoints() + " points");

//        ArrayList<Trophy> trophies;
//        // SET VALUES TO TROPHIES HERE
//        trophies = activeperson.getTrophies();
//
//        TrophyAdapter adapter = new TrophyAdapter(getContext(), trophies);
//
//        ListView listView = (ListView) view.findViewById(R.id.trophy_list);
//        // listView.setBackgroundColor(Color.parseColor("#8800A0"));
//
//        listView.setAdapter(adapter);

        Button trophybutton = (Button) view.findViewById(R.id.trophy_button);
        trophybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Trophy_Fragment newFragment = Trophy_Fragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("activeperson", activeperson);
                newFragment.setArguments(bundle);
                ft.replace(((ViewGroup)getView().getParent()).getId(), newFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Button redeembutton = (Button) view.findViewById(R.id.redeem_button);
        redeembutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServices();
            }
        });

        Button hofbutton = (Button) view.findViewById(R.id.hof_button);
        hofbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), HOFActivity.class);
                startActivity(mIntent);
            }
        });

        return view;
    }

    public void showServices() {
        final ArrayList<Service> services = new ArrayList<>();
        APIHelper.get("/services/available", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i=0;i<response.length(); i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);
                        services.add(new Service(
                                json.getString("s_Id"),
                                json.getString("s_Name"),
                                json.getInt("s_EmptySlots"),
                                json.getInt("s_Points")
                        ));
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (services.isEmpty()) {
                    Toast.makeText(getContext(), "Sorry, no available redeeming options.", Toast.LENGTH_LONG).show();
                } else {
                    Dialog myDialog = new Dialog(getActivity());
                    myDialog.setContentView(R.layout.redeem_view);
                    final ListView serviceList = (ListView) myDialog.findViewById(R.id.service_list);
                    serviceList.setAdapter(new ServiceAdapter(getContext(), services, getParentFragment(), activeperson));
                    myDialog.setCancelable(true);
                    myDialog.setTitle("ListView");
                    myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //refresh points
                            TextView balance = (TextView) getView().findViewById(R.id.balance);
                            balance.setText(activeperson.getPoints() + " points");
                        }
                    });
                    myDialog.show();
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

}

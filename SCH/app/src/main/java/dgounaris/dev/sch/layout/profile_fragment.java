package dgounaris.dev.sch.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import dgounaris.dev.sch.DBHelper.MyDBHelper;
import dgounaris.dev.sch.HOFActivity;
import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.adapter.ServiceAdapter;
import dgounaris.dev.sch.adapter.Trophy;
import dgounaris.dev.sch.adapter.TrophyAdapter;

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

        int is_from_home = getArguments().getInt("from_home", 0);
        if (is_from_home == 1) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyDBHelper mdb = new MyDBHelper(getContext());

                        TextView balance = (TextView) getActivity().findViewById(R.id.balance);
                        balance.setText(mdb.set_balance(10, activeperson.getId()) + " points");
                        activeperson.setPoints(activeperson.getPoints()+10);
                        Toast.makeText(getContext(),"10 points added!",Toast.LENGTH_SHORT).show();
                    }
            }, 3000);
        }
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

        ArrayList<Trophy> trophies;
        // SET VALUES TO TROPHIES HERE
        trophies = activeperson.getTrophies();

        TrophyAdapter adapter = new TrophyAdapter(getContext(), trophies);

        ListView listView = (ListView) view.findViewById(R.id.trophy_list);
        // listView.setBackgroundColor(Color.parseColor("#8800A0"));

        listView.setAdapter(adapter);

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
        ArrayList<Service> services;
        services = ((MainActivity) getActivity()).getAvailableServices();
        if (services.isEmpty()) {
            Toast.makeText(getContext(), "Sorry, no available redeeming options.", Toast.LENGTH_LONG).show();
        } else {
            Dialog myDialog = new Dialog(getActivity());
            myDialog.setContentView(R.layout.redeem_view);
            final ListView serviceList = (ListView) myDialog.findViewById(R.id.service_list);
            serviceList.setAdapter(new ServiceAdapter(getContext(), services, this));
            serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                    int[] sDet = ((ServiceAdapter) serviceList.getAdapter()).getCurrentServiceDetails(pos);
                    onRedeemPoints(sDet[0], sDet[1]);
                }
            });
            myDialog.setCancelable(true);
            myDialog.setTitle("ListView");
            myDialog.show();
        }
    }

    public void onRedeemPoints(int serviceid, int points) {
        int result = ((MainActivity) getActivity()).onRedeemPoints(serviceid, points);
        if (result >= 0) {
            Toast.makeText(getContext(), "Transaction successful", Toast.LENGTH_SHORT).show();
            TextView textView = (TextView) getView().findViewById(R.id.balance);
            textView.setText(result + " points");
            activeperson.setPoints(result);
        } else {
            Toast.makeText(getContext(), "Error processing request, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

}

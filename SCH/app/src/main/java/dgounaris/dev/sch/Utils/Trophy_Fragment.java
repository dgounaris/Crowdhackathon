package dgounaris.dev.sch.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.adapter.Trophy;
import dgounaris.dev.sch.adapter.TrophyAdapter;

import static dgounaris.dev.sch.R.id.container;

/**
 * Created by Rhogarj on 5/21/2017.
 */

public class Trophy_Fragment extends Fragment{

    private Person activeperson;

    public Trophy_Fragment() {
        // Required empty public constructor
    }

    public static Trophy_Fragment newInstance() {
        Trophy_Fragment fragment = new Trophy_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeperson = (Person) getArguments().getSerializable("activeperson");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trophy_fragment, container, false);
        ArrayList<Trophy> trophies;
        // SET VALUES TO TROPHIES HERE
        trophies = activeperson.getTrophies();

        TrophyAdapter adapter = new TrophyAdapter(getContext(), trophies);

        ListView listView = (ListView) view.findViewById(R.id.trophy_list);
        // listView.setBackgroundColor(Color.parseColor("#8800A0"));

        listView.setAdapter(adapter);
        return view;
    }

}


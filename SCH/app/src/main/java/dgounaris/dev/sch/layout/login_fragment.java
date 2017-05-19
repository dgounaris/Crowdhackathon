package dgounaris.dev.sch.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dgounaris.dev.sch.DBHelper.MyDBHelper;
import dgounaris.dev.sch.LoginActivity;
import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;

/**
 * Created by DimitrisLPC on 18/5/2017.
 */

public class login_fragment extends Fragment {

    EditText username;
    EditText password;
    Button login;
    Button to_register;
    MyDBHelper databaseHelper;

    public login_fragment() {
        // Required empty public constructor
    }

    public static login_fragment newInstance() {
        login_fragment fragment = new login_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new MyDBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_fragment, container, false);
        username = (EditText) view.findViewById(R.id.username_editable);
        password = (EditText) view.findViewById(R.id.password_editable);
        login = (Button) view.findViewById(R.id.login_button);
        to_register = (Button) view.findViewById(R.id.to_register_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginAccount(username.getText().toString(), password.getText().toString());
            }
        });
        return view;
    }

    public void onLoginAccount(String username, String password) {
        int id = databaseHelper.checkCredentials(username, password);
        if (id==-1) {
            Toast.makeText(getContext(), "Error: Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }
        Person person = databaseHelper.getPerson(id);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("activeperson", person);
        startActivity(intent);
    }

}

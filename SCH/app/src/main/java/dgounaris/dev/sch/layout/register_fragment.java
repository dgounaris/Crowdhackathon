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
import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;

/**
 * Created by DimitrisLPC on 20/5/2017.
 */

public class register_fragment extends Fragment {

    EditText username;
    EditText password;
    EditText password_reenter;
    EditText name;
    EditText surname;
    Button registerbtn;
    MyDBHelper databaseHelper;

    public register_fragment() {
        // Required empty public constructor
    }

    public static register_fragment newInstance() {
        register_fragment fragment = new register_fragment();
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
        View view = inflater.inflate(R.layout.fragment_register_fragment, container, false);
        username = (EditText) view.findViewById(R.id.username_editable);
        password = (EditText) view.findViewById(R.id.password_editable);
        password_reenter = (EditText) view.findViewById(R.id.password_reenter_editable);
        name = (EditText) view.findViewById(R.id.name_editable);
        surname = (EditText) view.findViewById(R.id.surname_editable);
        registerbtn = (Button) view.findViewById(R.id.register_button);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().equals("") && !password.getText().toString().equals("") && !password_reenter.getText().toString().equals("") && !name.getText().toString().equals("") && !surname.getText().toString().equals("")) {
                    if (databaseHelper.checkForUniqueUsername(username.getText().toString())) {
                        if (checkPasswordEntries(password.getText().toString(), password_reenter.getText().toString())) {
                            databaseHelper.registerAccount(username.getText().toString(), password.getText().toString(), name.getText().toString(), surname.getText().toString(), null);
                            int id = databaseHelper.checkCredentials(username.getText().toString(), password.getText().toString());
                            Person person = databaseHelper.getPerson(id);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("activeperson", person);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Error: Password entries do not match", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Error: Username already in use", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Error: All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean checkPasswordEntries(String password, String reenter) {
        if (password.equals(""))
            return false;
        return password.equals(reenter);
    }

}

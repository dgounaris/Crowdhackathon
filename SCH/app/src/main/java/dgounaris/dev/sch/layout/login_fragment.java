package dgounaris.dev.sch.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dgounaris.dev.sch.APIHandler.ApiClient;
import dgounaris.dev.sch.APIHandler.ApiInterface;
import dgounaris.dev.sch.LoginActivity;
import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DimitrisLPC on 18/5/2017.
 */

public class login_fragment extends Fragment {

    EditText username;
    EditText password;
    Button login;
    Button to_register;

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
                onLoginAttempt(username.getText().toString(), password.getText().toString());
            }
        });
        to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).toRegister();
            }
        });
        return view;
    }

    private void onLoginAttempt(String username, String password) {
        ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);
        Call<Person> loginCall = apiService.loginAttempt(username, password);
        loginCall.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.code()>=500) {
                    Toast.makeText(getContext(), "Couldn't reach server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code()>=400) {
                    Toast.makeText(getContext(), "Error: Bad input provided", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code()==204) {
                    Toast.makeText(getContext(), "Error: Invalid credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code()==200) {
                    Person person = response.body();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("activeperson", person);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                Log.d("ONLOGINERROR", t.toString());
                Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

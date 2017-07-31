package dgounaris.dev.sch.layout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.LoginActivity;
import dgounaris.dev.sch.MainActivity;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.R;
import dgounaris.dev.sch.Trophies.Trophy;

/**
 * Created by DimitrisLPC on 18/5/2017.
 */

public class login_fragment extends Fragment {

    EditText username;
    EditText password;
    Button login;
    Button to_register;
    APIHelper apiHelper;

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
        RequestParams rp = new RequestParams();
        rp.add("username", username); rp.add("password", password);
        APIHelper.post("/login", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (statusCode==204) {
                    Toast.makeText(getContext(), "Bad reply from server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i=0;i<response.length(); i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);
                        onLoginVerified(json.getString("c_Id"));
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Invalid credentials.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Invalid credentials.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Invalid credentials.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void onLoginVerified(final String id) {
        RequestParams rp = new RequestParams();
        rp.add("id", id);
        APIHelper.get("/person/" + id + "/details", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (statusCode==204) {
                    Toast.makeText(getContext(), "Bad reply from server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i=0;i<response.length(); i++) {
                    try {
                        final JSONObject json = response.getJSONObject(i);
                        JSONArray trophies = json.getJSONArray("p_Trophies");
                        final ArrayList<Trophy> myTrophies = new ArrayList<Trophy>();
                        for (int j=0;j<trophies.length(); j++) {
                            final JSONObject jsonTrophy = trophies.getJSONObject(j);
                            APIHelper.get("/trophy/" + jsonTrophy.getString("t_Id") + "/image", null, new FileAsyncHttpResponseHandler(getContext()) {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                    //todo get default trophy icon and still create trophy
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, File file) {
                                    String filePath = file.getPath();
                                    Bitmap myImg = BitmapFactory.decodeFile(filePath);
                                    try {
                                        myTrophies.add(new Trophy(
                                                jsonTrophy.getString("t_Id"),
                                                jsonTrophy.getString("t_Name"),
                                                jsonTrophy.getString("t_Description"),
                                                myImg
                                        ));
                                    } catch (JSONException e) {

                                    }
                                }
                            });
                        }
                        APIHelper.get("/person/" + id + "/image", null, new FileAsyncHttpResponseHandler(getContext()) {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                //todo get default picture and still call onPersonFetched after
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, File file) {
                                String filePath = file.getPath();
                                Bitmap myImg = BitmapFactory.decodeFile(filePath);
                                try {
                                    onPersonFetched(id, json.getString("p_Name"), json.getString("p_Surname"), json.getInt("p_Points"), json.getInt("p_TotalPoints"), myImg, myTrophies);
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), "Could not load user details.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Could not load user details.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), "Could not load user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPersonFetched(String id, String name, String surname, int points, int totalPoints, Bitmap img, ArrayList<Trophy> myTrophies) {
        Person person = new Person(
                id,
                name,
                surname,
                points,
                totalPoints,
                img,
                myTrophies
        );
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("activeperson", person);
        startActivity(intent);
    }

}

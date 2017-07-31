package dgounaris.dev.sch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.adapter.HallofFameAdapter;

public class HOFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hof);
        TopTotalPoints(5);
    }

    private void TopTotalPoints(int max) {
        final ArrayList<Person> myPeople = new ArrayList<>();
        APIHelper.get("/people/toppoints/" + ((Integer)max).toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);
                        myPeople.add(new Person(
                                json.getString("p_Id"),
                                json.getString("p_Name"),
                                json.getString("p_Surname"),
                                json.getInt("p_Points"),
                                json.getInt("p_TotalPoints")
                        ));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
                HallofFameAdapter hofadapter = new HallofFameAdapter(getApplicationContext(), myPeople);
                ListView hofList = (ListView) findViewById(R.id.hof_list);
                hofList.setAdapter(hofadapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (statusCode >= 500) {
                    Toast.makeText(getApplicationContext(), "Unable to reach server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (statusCode == 401) {
                    Toast.makeText(getApplicationContext(), "Could not process request. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode >= 500) {
                    Toast.makeText(getApplicationContext(), "Unable to reach server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (statusCode == 401) {
                    Toast.makeText(getApplicationContext(), "Could not process request. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode >= 500) {
                    Toast.makeText(getApplicationContext(), "Unable to reach server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (statusCode == 401) {
                    Toast.makeText(getApplicationContext(), "Could not process request. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


        });
    }

}

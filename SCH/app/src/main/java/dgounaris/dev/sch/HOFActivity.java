package dgounaris.dev.sch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dgounaris.dev.sch.APIHandler.ApiClient;
import dgounaris.dev.sch.APIHandler.ApiInterface;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.adapter.HallofFameAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HOFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hof);
        TopTotalPoints(5);
    }

    private void TopTotalPoints(int max) {
        ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<List<Person>> peopleCall = apiService.getTopByTotalPoints(max);
        peopleCall.enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                if (response.code()>=500) {
                    Toast.makeText(getApplicationContext(), "Couldn't reach server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code()>=400) {
                    Toast.makeText(getApplicationContext(), "Error: Bad input provided", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Person> people = response.body();
                HallofFameAdapter hofadapter = new HallofFameAdapter(getApplicationContext(), (ArrayList<Person>)people);
                ListView hofList = (ListView) findViewById(R.id.hof_list);
                hofList.setAdapter(hofadapter);
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

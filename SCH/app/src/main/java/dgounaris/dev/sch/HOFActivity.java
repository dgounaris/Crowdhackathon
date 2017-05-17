package dgounaris.dev.sch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import dgounaris.dev.sch.DBHelper.MyDBHelper;
import dgounaris.dev.sch.adapter.HallofFameAdapter;

public class HOFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hof);
        MyDBHelper databaseHelper = new MyDBHelper(getApplicationContext());
        HallofFameAdapter hofadapter = new HallofFameAdapter(this, databaseHelper.top_5());
        ListView hofList = (ListView) findViewById(R.id.hof_list);
        hofList.setAdapter(hofadapter);
    }

}

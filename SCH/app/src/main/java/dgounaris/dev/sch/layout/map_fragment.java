package dgounaris.dev.sch.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.Bins.Bin;
import dgounaris.dev.sch.R;

public class map_fragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    GoogleMap map;

    public map_fragment() {
        // Required empty public constructor
    }

    public static map_fragment newInstance() {
        map_fragment fragment = new map_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_map_fragment, container, false);
        mapView = (MapView) rootview.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);
        return rootview;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng athens = new LatLng(37.97945, 23.71622);
        getAllBins();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(athens).zoom(12).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.getUiSettings();

    }

    private void getAllBins() {
        final ArrayList<Bin> myBins = new ArrayList<>();
        APIHelper.get("/bins/all", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (statusCode==204) {
                    Toast.makeText(getContext(), "No bins available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i=0;i<response.length(); i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);
                        myBins.add(new Bin(
                                json.getString("b_Id"),
                                new LatLng(json.getDouble("b_Lat"), json.getDouble("b_Long")),
                                json.getInt("b_Space") == 1
                        ));
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
                for (int i=0;i<myBins.size();i++) {
                    map.addMarker(new MarkerOptions().position(myBins.get(i).getLatlong()).title("Bin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
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

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapview);
    }
}

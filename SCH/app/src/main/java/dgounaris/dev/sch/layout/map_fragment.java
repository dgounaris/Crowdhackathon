package dgounaris.dev.sch.layout;

import android.graphics.Bitmap;
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
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.APIHandler.ApiClient;
import dgounaris.dev.sch.APIHandler.ApiInterface;
import dgounaris.dev.sch.Bins.Bin;
import dgounaris.dev.sch.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Bin>> binCall = apiService.getAllBins();
        binCall.enqueue(new Callback<List<Bin>>() {
            @Override
            public void onResponse(Call<List<Bin>> call, Response<List<Bin>> response) {
                if (response.code()>=500) {
                    Toast.makeText(getContext(), "Couldn't reach server. Try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code()>=400) {
                    Toast.makeText(getContext(), "Error: Bad input provided", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code()==204) {
                    Toast.makeText(getContext(), "No bins available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Bin> myBins = response.body();
                addMarkers(myBins);
                addHeatmap(myBins);
            }

            @Override
            public void onFailure(Call<List<Bin>> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkers(List<Bin> myBins) {
        for (int i=0;i<myBins.size();i++) { //add marker for all bins, red if it's full, green otherwise
            float markerColor;
            if (myBins.get(i).hasSpace()) {
                markerColor = BitmapDescriptorFactory.HUE_GREEN;
            }
            else {
                markerColor = BitmapDescriptorFactory.HUE_RED;
            }
            map.addMarker(new MarkerOptions().position(myBins.get(i).getLatlong()).title("Bin").icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
        }
    }

    private void addHeatmap(List<Bin> myBins) {
        //todo
        // demo heatmap function. Bins need to have weights (normalized total values or number of recently added items) to determine active areas
        //Also needs utility and possible bug fixes
        List<WeightedLatLng> weightedLatLngs = new ArrayList<>();
        for (int i=0;i<myBins.size();i++) {
            weightedLatLngs.add(new WeightedLatLng(myBins.get(i).getLatlong(), Math.random()));
        }
        if (weightedLatLngs.size()==0) {
            return; //HeatmapTileProvider.Builder() does not accept empty data
        }
        HeatmapTileProvider heatmapProvider = new HeatmapTileProvider.Builder().weightedData(weightedLatLngs).radius(40).build();
        TileOverlayOptions overlayOptions = new TileOverlayOptions().tileProvider(heatmapProvider);
        map.addTileOverlay(overlayOptions);
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

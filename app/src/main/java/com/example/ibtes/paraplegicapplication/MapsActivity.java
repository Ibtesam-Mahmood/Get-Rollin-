package com.example.ibtes.paraplegicapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Coordinates;
import com.yelp.fusion.client.models.Review;
import com.yelp.fusion.client.models.Reviews;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    public interface ResponseCallBack{
        void onResponse(ArrayList<Review> response);
    }

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private YelpFusionApi yelpFusionApi;

    private LatLng position = null;

    private LinkedList<MarkerOptions> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String yelpApiKey = getString(R.string.yelp_fusion_api_key);

        try {
            yelpFusionApi =  new YelpFusionApiFactory().createAPI(yelpApiKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        markerList = new LinkedList<MarkerOptions>();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocation();

    }

    //Locates the current location for the user and moves the map to the location
    private void getLocation(){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Checks if the permission is granted
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            getLocation();
            return;

        }
        else{ //If the permission is granted

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                LatLng latLng =  new LatLng(location.getLatitude(), location.getLongitude());
                                //LatLng latLng =  new LatLng(40.762, -73.984);

                                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                                        latLng,
                                        12f
                                );
                                MarkerOptions marker =  new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                mMap.moveCamera(update);
                                mMap.addMarker(marker);

                                boolean firstTime = false;

                                if(position == null)
                                    firstTime = true;

                                position = latLng;

                                if(firstTime)
                                    yelpMarker("Athletics");
                            }
                        }
                    });
        }

    }

    public void yelpMarker(String term){

        Map<String, String> params =  new HashMap<>();

        params.put("term", term);
        params.put("latitude", position.latitude + "");
        params.put("longitude", position.longitude + "");

        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);

        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                deleteMarkers();

                SearchResponse searchResponse = response.body();
                if(searchResponse != null){
                    for (int i = 0; i < searchResponse.getBusinesses().size(); i++){
                        Business tempBusiness = searchResponse.getBusinesses().get(i);

                        Log.e("name", tempBusiness.getName());

                        Coordinates coordinates =  tempBusiness.getCoordinates();
                        String companyName = tempBusiness.getName();

                        LatLng markerLocation = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());

                        final MarkerOptions marker =  new MarkerOptions()
                                .position(markerLocation)
                                .title(companyName);

                        final ResponseCallBack callBack = new ResponseCallBack() {

                            @Override
                            public void onResponse(ArrayList<Review> response) {
                                setColorAndPost(marker, response, "rut");
                            }

                        };

                        busReviews(tempBusiness.getId(), callBack);

                    }
                }


            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                printToast("No results found", getApplicationContext());
            }
        };

        call.enqueue(callback);


    }

    public void setColorAndPost(MarkerOptions marker, ArrayList<Review> busRew, String search){

        for (int i = 0; i < busRew.size(); i++){

            String reviewContent = busRew.get(i).getText().toLowerCase();
            search = search.toLowerCase();

            Log.e("text", reviewContent);

            boolean contains = reviewContent.contains(search);

            if(contains)
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        }

        markerList.add(marker);
        mMap.addMarker(marker);

    }

    public void deleteMarkers(){

        mMap.clear();
        getLocation();

    }


    private void busReviews(String id, final ResponseCallBack callBack){


        Call<Reviews> call = yelpFusionApi.getBusinessReviews(id, "");

        Callback<Reviews> callback = new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                callBack.onResponse( response.body().getReviews() );

            }
            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                // HTTP error happened, do something to handle it.
            }
        };

        call.enqueue(callback);


    }

    //Prints a msg as a toast
    public static void printToast(String m, Context context){

        Toast.makeText(context, m, Toast.LENGTH_SHORT).show();

    }

    public void searchPressed(View v){

        EditText editText = findViewById(R.id.searchBar);

        String text = editText.getText().toString();

        if(text.isEmpty()){
            printToast("Search bar is empty", this);
        }
        else {
            yelpMarker(text);
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


    }
}

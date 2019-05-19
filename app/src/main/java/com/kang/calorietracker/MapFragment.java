package com.kang.calorietracker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.places.api.Places;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.kang.calorietracker.helper.Credential;
import com.kang.calorietracker.helper.GeoLocation;
import com.kang.calorietracker.helper.Parks;
import com.kang.calorietracker.helper.User;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    View vMap;
    GoogleMap map;
    MapView mapView;
    User loginUser;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = vMap.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SharedPreferences user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        loginUser = gson.fromJson(userJson, User.class);
        System.out.println(loginUser.address);
        GetGeoLocationAsyncTask getGeoLocationAsyncTask = new GetGeoLocationAsyncTask();
        getGeoLocationAsyncTask.execute(loginUser.address);
        return vMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
//        try {
//            map.setMyLocationEnabled(true);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }

        Places.initialize(getActivity(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(getActivity());
       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView

        //map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.1, -87.9)));



    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class GetGeoLocationAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String...params) {
            return RestClient.getGeoLocation(params[0]);
        }

        @Override
        protected void onPostExecute (String result) {
            if (result.equals("")) {
                return;
            }
            try {
                System.out.println(result);
                Gson gson = new Gson();
                GeoLocation geoLocation = gson.fromJson(result, GeoLocation.class);
                double lat = geoLocation.resourceSets[0].resources[0].point.coordinates[0];
                double lng = geoLocation.resourceSets[0].resources[0].point.coordinates[1];
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12);
                map.animateCamera(cameraUpdate);
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title("Home");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.snippet(loginUser.address);
                map.addMarker(markerOptions);
                GetParksAsyncTask getParksAsyncTask = new GetParksAsyncTask();
                getParksAsyncTask.execute(lat, lng);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetParksAsyncTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground (Double...params) {
            return RestClient.getParks(params[0], params[1]);
        }
        @Override
        protected void onPostExecute (String result) {
            System.out.println(result);
            Gson gson = new Gson();
            try {
                Parks parks = gson.fromJson(result, Parks.class);
                Parks.Response.Group.Item[] items = parks.response.groups[0].items;
                System.out.println("items" + items.length);
                int minDistance = items[0].venue.location.distance;
                System.out.println("min dis "+ minDistance);
                int position = 0;
                int i = 0;
                for (Parks.Response.Group.Item item : items) {
                    if (item.venue.location.distance < minDistance) {
                        position = i;
                        minDistance = item.venue.location.distance;
                    }
                    System.out.println(item.venue.location.distance);
                    double lat = item.venue.location.labeledLatLngs[0].lat;
                    double lng = item.venue.location.labeledLatLngs[0].lng;
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title(item.venue.name);
                    markerOptions.snippet(item.venue.location.address);
                    map.addMarker(markerOptions);
                    i += 1;
                }
                Parks.Response.Group.Item nearest = items[position];
                System.out.println("nearest" + position);
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(nearest.venue.location.labeledLatLngs[0].lat, nearest.venue.location.labeledLatLngs[0].lng)).title(nearest.venue.name);
                markerOptions.snippet(nearest.venue.location.address);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                map.addMarker(markerOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

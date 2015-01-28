package sunnyweather.rokuan.com.sunny.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.openweatherapi.OpenWeatherAPI;
import sunnyweather.rokuan.com.sunny.views.WeatherView;

/**
 * Created by Christophe on 27/01/2015.
 */
public class HomeFragmentSave extends SunnyFragment implements LocationListener {
    private TextView locationText;

    private List<WeatherView> views;
    private Handler handler;

    private View loadingView;
    private Location currentLocation;
    private LocationManager lm;

    private View mainView;
    private LayoutInflater inflater;

    private static final int REFRESH_TIMEOUT = 20000;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_home, null);
        views = new LinkedList<WeatherView>();
        locationText = (TextView)mainView.findViewById(R.id.current_location);
        lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler();

        TableLayout table = (TableLayout)mainView.findViewById(R.id.location_weather_grid);

        for (int i = 0; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                views.add((WeatherView) row.getChildAt(j));
            }
        }

        loadingView = mainView.findViewById(R.id.loading_frame);

        //updateData();
    }

    private void updateData() {
        refresh();
    }

    private void startLoading(){
        // TODO:
        /*if(updateButton != null) {
            updateButton.setEnabled(false);
        }*/
        loadingView.setVisibility(View.VISIBLE);
    }

    private void endLoading(){
        loadingView.setVisibility(View.INVISIBLE);
        /*if(updateButton != null) {
            updateButton.setEnabled(true);
        }*/
    }

    private void renderWeather(String place, List<ForecastInfo> results) {
        locationText.setText(place);

        for (int i = 0; i < results.size(); i++) {
            views.get(i).setForecastContent(results.get(i));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getLocation() {
        /*if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else {
            Toast.makeText(this.getActivity(), "Unable to retrieve location", Toast.LENGTH_LONG).show();
            endLoading();
            return;
        }*/

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Criteria criteria = new Criteria();
            //criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

            String provider = lm.getBestProvider(criteria, true);
            //String provider = LocationManager.GPS_PROVIDER;
            Looper looper = Looper.myLooper();

            Log.i("Sunny - Weather (Provider)", provider);

            final Handler myHandler = new Handler(looper);
            myHandler.postDelayed(new Runnable() {
                public void run() {
                    lm.removeUpdates(HomeFragmentSave.this);

                    if(currentLocation == null){
                        // TODO: afficher une erreur etc
                        Toast.makeText(HomeFragmentSave.this.getActivity(), "Unable to retrieve location", Toast.LENGTH_LONG).show();
                        endLoading();
                    }
                }
            }, REFRESH_TIMEOUT);

            currentLocation = null;
            lm.requestSingleUpdate(provider, this, null);
        } else {
            Toast.makeText(this.getActivity(), "Please enable location tracking", Toast.LENGTH_LONG).show();
            endLoading();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;

        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        Geocoder gcd = new Geocoder(this.getActivity(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            final String cityCode = addresses.get(0).getLocality() + "," +
                    addresses.get(0).getCountryCode();

            //if (addresses.size() > 0)
            //System.out.println(cityCode);

            new Thread() {
                public void run() {
                    Activity activity = HomeFragmentSave.this.getActivity();
                    Long countryId = OpenWeatherAPI.getPlaceId(activity, cityCode);

                    if(countryId == null){
                        // TODO: afficher erreur etc
                        endLoading();
                        Toast.makeText(activity, "Unable to resolve location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //final List<ForecastInfo> results = OpenWeatherAPI.getWeekForecast(HomeActivity.this, 2988507);
                    final List<ForecastInfo> results = OpenWeatherAPI.getWeekForecast(activity, countryId);

                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(cityCode, results);
                            endLoading();
                        }
                    });
                }
            }.start();
        } catch (IOException e) {
            //e.printStackTrace();
            // TODO: afficher une erreur etc
            endLoading();
            Toast.makeText(this.getActivity(), "Unable to resolve location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;*/
        return mainView;
    }

    @Override
    public void refresh() {
        if (!isNetworkAvailable()) {
            // TODO: afficher un message dans un layout
            Toast.makeText(this.getActivity(), "No connection available", Toast.LENGTH_SHORT).show();
            return;
        }

        startLoading();
        getLocation();
    }
}

//public class HomeFragment extends SunnyFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
//    private static final int REFRESH_TIMEOUT = 20000;
//
//    private TextView locationText;
//
//    private List<WeatherView> views;
//    private Handler handler;
//
//    private View loadingView;
//    private Location currentLocation;
//    //private LocationManager lm;
//
//    private View mainView;
//    private LayoutInflater inflater;
//    private LocationRequest mLocationRequest;
//    //private LocationClient locationClient;
//    private GoogleApiClient client;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//        inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mainView = inflater.inflate(R.layout.fragment_home, null);
//        views = new LinkedList<WeatherView>();
//        locationText = (TextView)mainView.findViewById(R.id.current_location);
//        //lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//        handler = new Handler();
//
//        TableLayout table = (TableLayout)mainView.findViewById(R.id.location_weather_grid);
//
//        for (int i = 0; i < table.getChildCount(); i++) {
//            TableRow row = (TableRow) table.getChildAt(i);
//
//            for (int j = 0; j < row.getChildCount(); j++) {
//                views.add((WeatherView) row.getChildAt(j));
//            }
//        }
//
//        loadingView = mainView.findViewById(R.id.loading_frame);
//        createLocationRequest();
//        client = new GoogleApiClient.Builder(this.getActivity(), this, this)
//                .addApi(LocationServices.API)
//                .build();
//        client.connect();
//
//
//        //updateData();
//    }
//
//    @Override
//    public void onPause(){
//        stopLocationUpdates();
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroy(){
//        stopLocationUpdates();
//        super.onDestroy();
//    }
//
//    private void updateData() {
//        refresh();
//    }
//
//    private void startLoading(){
//        // TODO:
//        /*if(updateButton != null) {
//            updateButton.setEnabled(false);
//        }*/
//        loadingView.setVisibility(View.VISIBLE);
//    }
//
//    private void endLoading(){
//        loadingView.setVisibility(View.INVISIBLE);
//        /*if(updateButton != null) {
//            updateButton.setEnabled(true);
//        }*/
//    }
//
//    private void renderWeather(String place, List<ForecastInfo> results) {
//        locationText.setText(place);
//
//        for (int i = 0; i < results.size(); i++) {
//            views.get(i).setForecastContent(results.get(i));
//        }
//    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//    protected void createLocationRequest(){
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        /*mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);*/
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        //startLocationUpdates();
//        refresh();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    protected void startLocationUpdates() {
//        if(client.isConnected()) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
//        }
//    }
//
//    protected void stopLocationUpdates(){
//        if(client.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
//        }
//    }
//
//    private void getLocation() {
//        /*if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        } else if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        } else {
//            Toast.makeText(this.getActivity(), "Unable to retrieve location", Toast.LENGTH_LONG).show();
//            endLoading();
//            return;
//        }*/
//
//        if(client.isConnected()){
//            Looper looper = Looper.myLooper();
//
//            currentLocation = null;
//            /*LocationServices.FusedLocationApi.requestLocationUpdates(
//                    client, mLocationRequest, this);*/
//            startLocationUpdates();
//
//            final Handler myHandler = new Handler(looper);
//            myHandler.postDelayed(new Runnable() {
//                public void run() {
//                    //lm.removeUpdates(HomeFragment.this);
//
//                    stopLocationUpdates();
//                    myHandler.removeCallbacks(this);
//
//                    if(currentLocation == null){
//                        // TODO: afficher une erreur etc
//                        Toast.makeText(HomeFragment.this.getActivity(), "Unable to retrieve location", Toast.LENGTH_LONG).show();
//                        endLoading();
//                    }
//                }
//            }, REFRESH_TIMEOUT);
//        } else {
//            Toast.makeText(this.getActivity(), "Please enable location tracking", Toast.LENGTH_LONG).show();
//            endLoading();
//            return;
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        currentLocation = location;
//
//        double latitude = currentLocation.getLatitude();
//        double longitude = currentLocation.getLongitude();
//
//        Geocoder gcd = new Geocoder(this.getActivity(), Locale.getDefault());
//
//        try {
//            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
//            final String cityCode = addresses.get(0).getLocality() + "," +
//                    addresses.get(0).getCountryCode();
//
//            //if (addresses.size() > 0)
//            //System.out.println(cityCode);
//
//            new Thread() {
//                public void run() {
//                    Activity activity = HomeFragment.this.getActivity();
//                    Long countryId = OpenWeatherAPI.getPlaceId(activity, cityCode);
//
//                    if(countryId == null){
//                        // TODO: afficher erreur etc
//                        endLoading();
//                        Toast.makeText(activity, "Unable to resolve location", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    //final List<ForecastInfo> results = OpenWeatherAPI.getWeekForecast(HomeActivity.this, 2988507);
//                    final List<ForecastInfo> results = OpenWeatherAPI.getWeekForecast(activity, countryId);
//
//                    handler.post(new Runnable() {
//                        public void run() {
//                            renderWeather(cityCode, results);
//                            endLoading();
//                        }
//                    });
//                }
//            }.start();
//        } catch (IOException e) {
//            //e.printStackTrace();
//            // TODO: afficher une erreur etc
//            endLoading();
//            Toast.makeText(this.getActivity(), "Error while retrieving data", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        /*View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//        return rootView;*/
//        return mainView;
//    }
//
//    @Override
//    public void refresh() {
//        if (!isNetworkAvailable()) {
//            // TODO: afficher un message dans un layout
//            Toast.makeText(this.getActivity(), "No connection available", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        startLoading();
//        getLocation();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }
//}

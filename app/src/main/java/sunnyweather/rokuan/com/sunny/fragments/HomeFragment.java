package sunnyweather.rokuan.com.sunny.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.IPAPI;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.api.OpenWeatherAPI;
import sunnyweather.rokuan.com.sunny.data.LocationInfo;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;
import sunnyweather.rokuan.com.sunny.utils.Utils;
import sunnyweather.rokuan.com.sunny.views.ForecastDialog;
import sunnyweather.rokuan.com.sunny.views.WeatherView;

/**
 * The first fragment that displays the weather data for the current location
 */
public class HomeFragment extends SunnyFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int REFRESH_TIMEOUT = 20000;
    private static final String LOCATION_KEY = "location";

    private TextView locationText;

    private List<WeatherView> views;
    private List<ForecastInfo> infos;

    private View loadingView;

    private View mainView;
    private LayoutInflater inflater;

    private MenuItem refreshButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private boolean clientConnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
        }

        inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_home, null);
        loadingView = mainView.findViewById(R.id.loading_frame);
        views = new LinkedList<WeatherView>();
        locationText = (TextView)mainView.findViewById(R.id.current_location);

        TableLayout table = (TableLayout)mainView.findViewById(R.id.location_weather_grid);

        for (int i = 0; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                WeatherView weatherView = (WeatherView) row.getChildAt(j);
                weatherView.setOnClickListener(this);
                views.add(weatherView);
            }
        }

        /*if(savedInstanceState == null || !savedInstanceState.containsKey("forecastData")){
            // TODO: charger les donnees
            refresh();
        } else {

        }*/
        refresh();
    }

    @Override
    public void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume(){
        super.onResume();

        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if(clientConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    public Location getCurrentLocation(){
        return mCurrentLocation;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        inflater.inflate(R.menu.home, menu);
        refreshButton = menu.findItem(R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_refresh:
                this.refresh();
                return true;

            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(bundle);
    }

    /**
     * Starts the loading process
     */
    private void startLoading(){
        // TODO:
        if(refreshButton != null) {
            //refreshButton.setEnabled(false);
            refreshButton.setVisible(false);
        }
        loadingView.setVisibility(View.VISIBLE);
    }

    private void endLoading(boolean success, ForecastData forecast){

    }

    /**
     * Ends the loading process
     * @param success the process result (true if everything went fine, false otherwise)
     */
    private void endLoading(boolean success){
        // TODO: afficher des messages selon la reussite de l'operation
        loadingView.setVisibility(View.INVISIBLE);
        if(refreshButton != null) {
            //refreshButton.setEnabled(true);
            refreshButton.setVisible(true);
        }
    }

    /**
     * Render the weather data into the fragment
     * @param place the current place
     * @param results the weather results
     */
    private void renderWeather(String place, List<ForecastInfo> results) {
        infos = results;
        locationText.setText(place);

        for (int i = 0; i < results.size(); i++) {
            views.get(i).setForecastContent(results.get(i));
        }
    }

    /*
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/

    /**
     * Tries to find current user location
     */
    /*private void getLocation() {
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    final LocationInfo lInfo = IPAPI.getLocation();
                    final Activity activity = HomeFragment.this.getActivity();
                    Long countryId = OpenWeatherAPI.getPlaceId(activity, lInfo.getPlaceCode());

                    if (countryId == null) {
                        // TODO: afficher erreur etc
                        handler.post(new Runnable(){
                            public void run(){
                                endLoading(false);
                                Toast.makeText(activity, "Unable to resolve location", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    final List<ForecastInfo> results = OpenWeatherAPI.getWeekForecast(activity, countryId);

                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(lInfo.getPlaceCode(), results);
                            endLoading(true);
                        }
                    });
                }catch(Exception e){
                    handler.post(new Runnable(){
                        public void run(){
                            endLoading(false);
                        }
                    });
                }
            }
        }).start();
    }*/

    @Override
    public void onConnected(Bundle connectionHint) {
        clientConnected = true;
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;*/
        this.setHasOptionsMenu(true);
        return mainView;
    }

    @Override
    public void refresh() {
        if (!Utils.isNetworkAvailable(this.getActivity())) {
            // TODO: afficher un message dans un layout
            Toast.makeText(this.getActivity(), "No connection available", Toast.LENGTH_SHORT).show();
            return;
        }

        startLoading();
        getLocation();
    }

    @Override
    public void onClick(View v) {
        try{
            ForecastDialog.newInstance(((WeatherView)v).getForecastContent()).show(this.getActivity().getSupportFragmentManager(), "FORECAST_DLG");
        } catch(Exception e){
            return;
        }
    }
}

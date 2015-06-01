package sunnyweather.rokuan.com.sunny.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.openweather.ForecastData;
import sunnyweather.rokuan.com.sunny.api.openweather.ForecastDataResultCallback;
import sunnyweather.rokuan.com.sunny.api.openweather.OpenWeatherMapAPI;
import sunnyweather.rokuan.com.sunny.utils.Utils;
import sunnyweather.rokuan.com.sunny.views.ForecastDialog;
import sunnyweather.rokuan.com.sunny.views.ForecastView;

/**
 * The first fragment that displays the weather data for the current location
 */
public class HomeFragment extends SunnyFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ForecastDataResultCallback {
    private static final int REFRESH_TIMEOUT = 20000;
    private static final String LOCATION_KEY = "location";

    private View mainView;
    private LayoutInflater inflater;
    //@InjectView(R.id.multi_state_view) protected MultiStateView dataFrame;
    @InjectView(R.id.fragment_home_view_placeholder) protected ViewGroup forecastPlaceHolder;

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

        ButterKnife.inject(this, mainView);

        //dataFrame.set
        //refresh();
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
    }

    private void endLoading(boolean success){
        if(refreshButton != null) {
            //refreshButton.setEnabled(false);
            refreshButton.setVisible(true);
        }
    }

    private void getForecastData(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(OpenWeatherMapAPI.getWeekForecastForLocationURL(this.getActivity(), getCurrentLocation()), OpenWeatherMapAPI.getAdditionalParameters(this.getActivity()), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                HomeFragment.this.startLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                try {
                    HomeFragment.this.onForecastDataResult(true, ForecastData.buildFromJSON(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    HomeFragment.this.onForecastDataResult(false, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                HomeFragment.this.onForecastDataResult(false, null);
            }

            /*@Override
            public void onFinish() {
                HomeFragment.this.endLoading();
            }*/
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        clientConnected = true;
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
        getForecastData();
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
        getForecastData();
    }

    @Override
    public void onClick(View v) {
        /*try{
            ForecastDialog.newInstance(((WeatherView)v).getForecastContent()).show(this.getActivity().getSupportFragmentManager(), "FORECAST_DLG");
        } catch(Exception e){
            return;
        }*/
    }

    @Override
    public void onForecastDataResult(boolean success, ForecastData result) {
        if(success){
            forecastPlaceHolder.removeAllViews();
            forecastPlaceHolder.addView(new ForecastView(this.getActivity(), result));
        } else {
            // TODO:
        }

        endLoading(success);
    }
}

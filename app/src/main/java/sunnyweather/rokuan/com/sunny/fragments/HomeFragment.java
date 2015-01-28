package sunnyweather.rokuan.com.sunny.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.openweatherapi.OpenWeatherAPI;
import sunnyweather.rokuan.com.sunny.views.WeatherView;

/**
 * Created by Christophe on 27/01/2015.
 */
public class HomeFragment extends SunnyFragment {
    private static final int REFRESH_TIMEOUT = 20000;

    private TextView locationText;

    private List<WeatherView> views;
    private Handler handler;

    private View loadingView;
    private Location currentLocation;

    private View mainView;
    private LayoutInflater inflater;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_home, null);
        views = new LinkedList<WeatherView>();
        locationText = (TextView)mainView.findViewById(R.id.current_location);
        handler = new Handler();

        TableLayout table = (TableLayout)mainView.findViewById(R.id.location_weather_grid);

        for (int i = 0; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                views.add((WeatherView) row.getChildAt(j));
            }
        }

        loadingView = mainView.findViewById(R.id.loading_frame);
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
        Looper looper = Looper.myLooper();

        currentLocation = null;

        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    URL url = new URL("http://ipinfo.io/json");
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder jsonString = new StringBuilder();

                    while((line = br.readLine()) != null){
                        jsonString.append(line);
                    }

                    JSONObject json = new JSONObject(jsonString.toString());
                    br.close();

                    final String cityCode = json.getString("city") + "," + json.getString("country");
                    String locationString = json.getString("loc");
                    String[] locationFields = locationString.split(",");

                    currentLocation = new Location("");
                    currentLocation.setLatitude(Double.parseDouble(locationFields[0]));
                    currentLocation.setLongitude(Double.parseDouble(locationFields[1]));

                    //fetchWeather(cityCode);

                    Activity activity = HomeFragment.this.getActivity();
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
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        final Handler myHandler = new Handler(looper);
        myHandler.postDelayed(new Runnable() {
            public void run() {
                myHandler.removeCallbacks(this);

                if(currentLocation == null){
                    // TODO: afficher une erreur etc
                    Toast.makeText(HomeFragment.this.getActivity(), "Unable to retrieve location", Toast.LENGTH_LONG).show();
                    endLoading();
                }
            }
        }, REFRESH_TIMEOUT);
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

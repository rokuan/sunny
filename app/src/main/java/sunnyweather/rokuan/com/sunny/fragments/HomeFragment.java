package sunnyweather.rokuan.com.sunny.fragments;

import android.app.Activity;
import android.content.Context;
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
import java.util.LinkedList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.IPAPI;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.api.OpenWeatherAPI;
import sunnyweather.rokuan.com.sunny.data.LocationInfo;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;
import sunnyweather.rokuan.com.sunny.views.ForecastDialog;
import sunnyweather.rokuan.com.sunny.views.WeatherView;

/**
 * Created by Christophe on 27/01/2015.
 */
public class HomeFragment extends SunnyFragment implements View.OnClickListener {
    private static final int REFRESH_TIMEOUT = 20000;

    private TextView locationText;

    private List<WeatherView> views;
    private List<ForecastInfo> infos;
    private Handler handler;

    private View loadingView;

    private View mainView;
    private LayoutInflater inflater;

    private MenuItem refreshButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_home, null);
        loadingView = mainView.findViewById(R.id.loading_frame);
        views = new LinkedList<WeatherView>();
        locationText = (TextView)mainView.findViewById(R.id.current_location);
        handler = new Handler();

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

    /*@Override
    public void onSaveInstanceState(Bundle bundle){
        if(infos != null){
            //bundle.putParcelableArray();
        }
    }*/

    private void startLoading(){
        // TODO:
        if(refreshButton != null) {
            //refreshButton.setEnabled(false);
            refreshButton.setVisible(false);
        }
        loadingView.setVisibility(View.VISIBLE);
    }

    private void endLoading(boolean success){
        // TODO: afficher des messages selon la reussite de l'operation
        loadingView.setVisibility(View.INVISIBLE);
        if(refreshButton != null) {
            //refreshButton.setEnabled(true);
            refreshButton.setVisible(true);
        }
    }

    private void renderWeather(String place, List<ForecastInfo> results) {
        infos = results;
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
        if (!isNetworkAvailable()) {
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

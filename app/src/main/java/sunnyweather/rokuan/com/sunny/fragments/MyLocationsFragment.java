package sunnyweather.rokuan.com.sunny.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.Place;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;
import sunnyweather.rokuan.com.sunny.db.SunnySQLiteOpenHelper;
import sunnyweather.rokuan.com.sunny.openweatherapi.OpenWeatherAPI;
import sunnyweather.rokuan.com.sunny.views.LocationWeatherView;

/**
 * Created by Christophe on 27/01/2015.
 */
public class MyLocationsFragment extends SunnyFragment {
    private AutoCompleteTextView searchBar;
    private ArrayAdapter<Place> placesAdapter;
    private ListView weatherList;
    private View mainView;

    private Handler handler;
    private AtomicInteger stamp;
    private static final int TEXT_DELAY = 1000;

    private SunnySQLiteOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        handler = new Handler();
        db = new SunnySQLiteOpenHelper(this.getActivity());

        LayoutInflater inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_location, null);

        searchBar = (AutoCompleteTextView)mainView.findViewById(R.id.search_bar);
        weatherList = (ListView)mainView.findViewById(R.id.weather_list);

        stamp = new AtomicInteger(0);

        /*if(searchBar.getText().length() > 0) {
            updateSearchResults();
        }*/
        updateSearchResults();
        updateLocations();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSearchResults();
            }
        });

        /*searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Place> matchingPlaces = OpenWeatherAPI.queryPlaces(MyLocationsFragment.this.getActivity(), s.toString());
                searchBar.setAdapter(new PlaceSuggestionAdapter(MyLocationsFragment.this.getActivity(), R.layout.place_result_item, matchingPlaces));
                final String searchString = s.toString();

                new Thread(new Runnable(){
                   public void run(){
                       List<Place> matchingPlaces = OpenWeatherAPI.queryPlaces(MyLocationsFragment.this.getActivity(), searchString.toString());
                       setSearchResults(matchingPlaces);
                   }
                }).start();

                /*Looper myLooper = Looper.myLooper();
                Handler handler = new Handler(myLooper);
                handler.post(new Runnable(){
                    public void run(){
                        List<Place> matchingPlaces = OpenWeatherAPI.queryPlaces(MyLocationsFragment.this.getActivity(), searchString.toString());
                        setSearchResults(matchingPlaces);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        weatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO:
                //displayWeather(id);
            }
        });
    }

    private void updateSearchResults(){
        if(searchBar.getText().length() > 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<Place> matchingPlaces = OpenWeatherAPI.queryPlaces(MyLocationsFragment.this.getActivity(), searchBar.getText().toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            searchBar.setAdapter(new PlaceSuggestionAdapter(MyLocationsFragment.this.getActivity(), R.layout.place_result_item, matchingPlaces));
                        }
                    });
                }
            }).start();
        }
    }

    private void updateLocations(){

    }

    private void setSearchResults(List<Place> results){
        searchBar.setAdapter(new PlaceSuggestionAdapter(MyLocationsFragment.this.getActivity(), R.layout.place_result_item, results));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void refresh() {
        List<Place> cities = db.queryAllCities();

    }

    class PlaceSuggestionAdapter extends ArrayAdapter<Place> {
        private LayoutInflater inflater;
        private SunnySQLiteOpenHelper db;

        public PlaceSuggestionAdapter(Context context, int resource, List<Place> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            db = new SunnySQLiteOpenHelper(context);
        }

        @Override
        public long getItemId(int position){
            return this.getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = inflater.inflate(R.layout.place_result_item, parent, false);
            }

            if(position % 2 == 0){
                convertView.setBackgroundColor(Color.WHITE);
            } else {
                convertView.setBackgroundColor(Color.LTGRAY);
            }

            final Place place = this.getItem(position);
            TextView placeName = (TextView)convertView.findViewById(R.id.place_item_name);
            ImageButton addPlace = (ImageButton)convertView.findViewById(R.id.place_item_add);

            placeName.setText(place.getName());

            if(db.cityExists(place.getId())){
                addPlace.setVisibility(View.INVISIBLE);
            } else {
                addPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO:
                        db.addCity(place);
                    }
                });
            }

            return convertView;
        }
    }

    class PlaceAdapter extends ArrayAdapter<Place> {
        private LayoutInflater inflater;

        public PlaceAdapter(Context context, int resource, List<Place> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                //convertView = inflater.inflate(R.layout.weather_item, parent, false);
                convertView = new LocationWeatherView(this.getContext(), this.getItem(position));
            }

            return convertView;
        }
    }
}

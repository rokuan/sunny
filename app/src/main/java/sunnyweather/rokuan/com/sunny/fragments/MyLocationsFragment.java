package sunnyweather.rokuan.com.sunny.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.openweather.City;
import sunnyweather.rokuan.com.sunny.api.openweather.OpenWeatherMapAPI;
import sunnyweather.rokuan.com.sunny.api.openweather.WeatherData;
import sunnyweather.rokuan.com.sunny.db.SunnySQLiteOpenHelper;
import sunnyweather.rokuan.com.sunny.views.CityWeatherView;
import sunnyweather.rokuan.com.sunny.views.LocationWeatherView;
import sunnyweather.rokuan.com.sunny.views.WeatherView;

/**
 * Created by Christophe on 27/01/2015.
 */
public class MyLocationsFragment extends SunnyFragment {
    private AutoCompleteTextView searchBar;
    //private PlaceAdapter placesAdapter;
    private CitySuggestionAdapter suggestionsAdapter;
    private ListView weatherList;
    private View mainView;

    private SunnySQLiteOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        db = new SunnySQLiteOpenHelper(this.getActivity());

        LayoutInflater inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_location, null);

        searchBar = (AutoCompleteTextView)mainView.findViewById(R.id.search_bar);
        weatherList = (ListView)mainView.findViewById(R.id.weather_list);

        searchBar.setThreshold(3);
        //searchBar.set

        suggestionsAdapter = new CitySuggestionAdapter(MyLocationsFragment.this.getActivity(), R.layout.place_result_item);
        suggestionsAdapter.setNotifyOnChange(true);
        searchBar.setAdapter(suggestionsAdapter);

        // TODO: retablir le contenu de la barre de recherche

        updateLocations();

        /*weatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO:
                //displayWeather(id);
            }
        });*/
        /*weatherList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });*/
        this.registerForContextMenu(weatherList);
    }

    /**
     * Updates the user favorite locations data
     */
    private void updateLocations(){
        List<City> cities = db.queryAllCities();
        List<CityWeatherView> views = new ArrayList<>(cities.size());

        for(int i=0; i<cities.size(); i++){
            views.add(new CityWeatherView(this.getActivity(), cities.get(i)));
        }

        weatherList.setAdapter(new CityWeatherAdapter(this.getActivity(), R.layout.weather_item, views));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, v, menuInfo);
        Log.i("onCreateContextMenu", "entered");

        if (v.getId() == R.id.weather_list) {
            Log.i("onCreateContextMenu", "source: weather_list");
            MenuInflater inflater = this.getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_location_item, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //info.id
        //info.position;

        /*PlaceAdapter locationAdapter = (PlaceAdapter)weatherList.getAdapter();
        City p = locationAdapter.getItem(info.position);

        switch (item.getItemId()) {
            case R.id.delete_place:
                db.deleteCity(p.getId());
                locationAdapter.remove(p);
                locationAdapter.notifyDataSetChanged();
                return true;

            case R.id.move_place:
                // TODO:
                //info.targetView.startDrag();
                return true;

            default:
                break;
        }*/

        return super.onContextItemSelected(item);
    }

    @Override
    public void refresh() {
        updateLocations();
    }

    class CitySuggestionAdapter extends ArrayAdapter<City> {
        private LayoutInflater inflater;
        private SunnySQLiteOpenHelper db;
        private List<City> resultsList = new ArrayList<>();
        private Context context;

        public CitySuggestionAdapter(Context c, int resource) {
            super(c, resource);
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            db = new SunnySQLiteOpenHelper(c);
            context = c;

        }

        @Override
        public int getCount() {
            return resultsList.size();
        }

        @Override
        public City getItem(int index) {
            return resultsList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {
                        SyncHttpClient client = new SyncHttpClient();

                        client.get(OpenWeatherMapAPI.getCityQueryURL(constraint.toString()), OpenWeatherMapAPI.getAdditionalParameters(context), new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                                try {
                                    resultsList = new ArrayList<City>(result.getInt("count"));
                                    JSONArray array = result.getJSONArray("list");

                                    for(int i=0; i<array.length(); i++){
                                        JSONObject element = array.getJSONObject(i);
                                        resultsList.add(City.buildFromJSON(element));
                                    }
                                } catch (JSONException e) {
                                    Log.e("Sunny (Place query)", e.getMessage());
                                    // TODO
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                // TODO:
                            }
                        });

                        /*List<Place> results = new ArrayList<Place>();
                        JSONObject jsonResults = getJSON(context, String.format(PLACE_NAME_QUERY, name));

                        if(jsonResults != null){
                            try {
                                results = new ArrayList<Place>(jsonResults.getInt("count"));
                                JSONArray array = jsonResults.getJSONArray("list");

                                for(int i=0; i<array.length(); i++){
                                    JSONObject element = array.getJSONObject(i);
                                    results.add(Place.buildFromJSON(element));
                                }
                            } catch (JSONException e) {
                                Log.e("Sunny (Place query)", e.getMessage());
                                return results;
                            }
                        }

                        return results;
                        resultList = OpenWeatherMapAPI.queryPlaces(PlaceSuggestionAdapter.this.getContext(), constraint.toString());*/

                        filterResults.values = resultsList;
                        filterResults.count = resultsList.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
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

            final City place = this.getItem(position);
            TextView placeName = (TextView)convertView.findViewById(R.id.place_item_name);
            final ImageButton addPlace = (ImageButton)convertView.findViewById(R.id.place_item_add);

            placeName.setText(place.toString());

            if(db.cityExists(place.getId())){
                addPlace.setVisibility(View.INVISIBLE);
            } else {
                addPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO:
                        //Toast.makeText(PlaceSuggestionAdapter.this.getContext(), "Adding place: " + place.getName(), Toast.LENGTH_SHORT).show();
                        db.addCity(place);
                        addPlace.setVisibility(View.INVISIBLE);
                        try{
                            CityWeatherAdapter placeAdapter = (CityWeatherAdapter)MyLocationsFragment.this.weatherList.getAdapter();
                            placeAdapter.addCity(place);
                            placeAdapter.notifyDataSetChanged();
                        }catch(Exception e){
                            MyLocationsFragment.this.refresh();
                        }
                        //MyLocationsFragment.this.refresh();
                    }
                });
            }

            return convertView;
        }
    }

    /*class PlaceAdapter extends ArrayAdapter<City> {
        private LayoutInflater inflater;

        public PlaceAdapter(Context context, int resource, List<City> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //if(convertView == null){
                //convertView = inflater.inflate(R.layout.weather_item, parent, false);
                LocationWeatherView locView = new LocationWeatherView(this.getContext(), this.getItem(position));
                convertView = locView;
                locView.refreshData();
            //}

            return convertView;
        }
    }*/

    class CityWeatherAdapter extends ArrayAdapter<CityWeatherView> {
        private List<CityWeatherView> views = new ArrayList<>();

        public CityWeatherAdapter(Context context, int resource, List<CityWeatherView> objects) {
            super(context, resource, objects);
            views.addAll(objects);
        }

        @Override
        public int getCount(){
            return views.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return views.get(position);
        }

        public void addCity(City c){
            views.add(new CityWeatherView(this.getContext(), c));
            this.notifyDataSetChanged();
        }
    }
}

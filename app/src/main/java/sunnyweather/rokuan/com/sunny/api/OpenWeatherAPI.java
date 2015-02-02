package sunnyweather.rokuan.com.sunny.api;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.data.Place;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;
import sunnyweather.rokuan.com.sunny.utils.Utils;

/**
 * Created by Christophe on 24/01/2015.
 */
public class OpenWeatherAPI {
    private static final String OPENWEATHER_API_ADDRESS = "http://api.openweathermap.org/data/2.5/";
    private static final String OPENWEATHER_ICON_ADDRESS = "http://openweathermap.org/img/w/%s.png";

    private static final String PLACE_NAME_QUERY = OPENWEATHER_API_ADDRESS + "find?q=%s&type=like&cnt=4";
    private static final String WEATHER_QUERY = OPENWEATHER_API_ADDRESS + "weather?id=%s";
    private static final String FORECAST_QUERY = OPENWEATHER_API_ADDRESS + "forecast/daily?id=%s&units=metric&cnt=%d";
    private static final String ICON_QUERY = OPENWEATHER_ICON_ADDRESS;

    private static String getApiKey(Context context){
        return context.getResources().getString(R.string.weather_api_key);
    }

    //public static Bitmap getIcon(Context context, String iconName){
    public static Bitmap getIcon(String iconName){
        return Utils.getBitmapFromURL(String.format(ICON_QUERY, iconName));
    }

    public static Long getPlaceId(Context context, String name){
        JSONObject results = getJSON(context, String.format(PLACE_NAME_QUERY, name));

        try{
            return results.getJSONArray("list").getJSONObject(0).getLong("id");
        } catch(Exception e){
            return null;
        }
    }

    public static List<Place> queryPlaces(Context context, String name){
        List<Place> results = new ArrayList<Place>();
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
                Log.e("Sunny - Weather (Place query)", e.getMessage());
                return results;
            }
        }

        return results;
    }

    public static List<ForecastInfo> getWeekForecast(Context context, long placeId){
        return getForecast(context, placeId, 7);
    }

    public static List<ForecastInfo> getForecast(Context context, long placeId, int count){
        List<ForecastInfo> results = null;

        try {
            String query = String.format(FORECAST_QUERY, placeId, count);
            Log.i("Sunny - Weather (Forecast query)", query);
            JSONObject json = getJSON(context, query);
            int resultsSize = json.getInt("cnt");
            JSONArray elements = json.getJSONArray("list");

            // TODO: verifier que cnt == count ?
            //if(elements.length() > 0) {
            if(resultsSize > 0){
                //results = new ArrayList<ForecastInfo>(elements.length());
                results = new ArrayList<ForecastInfo>(resultsSize);

                //for (int i = 0; i <elements.length(); i++) {
                for (int i = 0; i <resultsSize; i++) {
                    JSONObject item = elements.getJSONObject(i);
                    //results.add(WeatherInfo.buildFromJSON(item));
                    results.add(ForecastInfo.buildFromJSON(item));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }

    public static WeatherInfo getWeather(Context context, long placeId){
        JSONObject result = getJSON(context, String.format(WEATHER_QUERY, placeId));

        try {
            return WeatherInfo.buildFromJSON(result);
        } catch (JSONException e) {
            return null;
        }
    }

    private static JSONObject getJSON(Context context, String address){
        ContentValues properties = new ContentValues();
        properties.put("x-api-key", getApiKey(context));
        JSONObject result = Utils.getJSON(address, properties);

        try {
            if (result.getInt("cod") != 200) {
                return null;
            }
        }catch(Exception e){
            return null;
        }

        return result;
    }
}
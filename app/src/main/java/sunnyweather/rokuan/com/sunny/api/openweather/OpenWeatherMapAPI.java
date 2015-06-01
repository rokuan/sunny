package sunnyweather.rokuan.com.sunny.api.openweather;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Locale;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.utils.RemoteData;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class OpenWeatherMapAPI {
    private static final String OPENWEATHER_API_ADDRESS = "http://api.openweathermap.org/data/2.5/";
    private static final String OPENWEATHER_ICON_ADDRESS = "http://openweathermap.org/img/w/%s.png";

    private static final String PLACE_NAME_QUERY = OPENWEATHER_API_ADDRESS + "find?q=%s&type=like&cnt=4";
    /*private static final String WEATHER_QUERY = OPENWEATHER_API_ADDRESS + "weather?id=%s";
    private static final String FORECAST_QUERY = OPENWEATHER_API_ADDRESS + "forecast/daily?id=%s&units=metric&cnt=%d";*/
    private static final String WEATHER_QUERY = OPENWEATHER_API_ADDRESS + "weather?";
    private static final String FORECAST_QUERY = OPENWEATHER_API_ADDRESS + "forecast/daily?units=metric&cnt=%d&";
    private static final String ICON_QUERY = OPENWEATHER_ICON_ADDRESS;

    private static final int ID_QUERY_TYPE = 0;
    private static final int LOCATION_QUERY_TYPE = 1;
    private static final int NAME_QUERY_TYPE = 2;

    private static String getApiKey(Context context){
        return context.getResources().getString(R.string.openweathermap_api_key);
    }

    //public static Bitmap getIcon(Context context, String iconName){
    public static Bitmap getIcon(String iconName){
        return RemoteData.getBitmapFromURL(String.format(ICON_QUERY, iconName));
    }

    public static String getBitmapURL(String iconName){
        return String.format(ICON_QUERY, iconName);
    }

    /**
     * Returns the forecast for a specific city denoted by its ID for 7 days
     * @param placeId the place id
     * @return a forecast for the next 7 days or null if an error occurred
     */
    public static String getWeekForecastForIdURL(long placeId){
        return getForecastURL(7, ID_QUERY_TYPE, placeId, null, null);
    }

    public static String getWeekForecastForNameURL(String placeName){
        return getForecastURL(7, NAME_QUERY_TYPE, -1, placeName, null);
    }

    public static String getWeekForecastForLocationURL(Location placeLocation){
        return getForecastURL(7, LOCATION_QUERY_TYPE, -1, null, placeLocation);
    }

    private static String getForecastURL(int count, int queryType, long placeId, String placeName, Location placeLocation){
        return addQueryParameter(String.format(FORECAST_QUERY, count), queryType, placeId, placeName, placeLocation);
    }

    /**
     * Gets a single weather data for the specified city
     * @param placeId the city id
     * @return a weather data or null if an error occurred
     */
    public static String getWeatherForIdURL(long placeId){
        return getWeatherURL(ID_QUERY_TYPE, placeId, null, null);
    }

    public static String getWeatherForNameURL(String placeName){
        return getWeatherURL(NAME_QUERY_TYPE, -1, placeName, null);
    }

    public static String getWeatherForLocationURL(Location placeLocation){
        return getWeatherURL(LOCATION_QUERY_TYPE, -1, null, placeLocation);
    }

    private static String getWeatherURL(int queryType, long placeId, String placeName, Location placeLocation){
        return addQueryParameter(WEATHER_QUERY, queryType, placeId, placeName, placeLocation);
    }

    public static String getCityQueryURL(String cityName){
        return String.format(PLACE_NAME_QUERY, cityName);
    }

    private static String addQueryParameter(String initialQuery, int queryType, long placeId, String placeName, Location placeLocation){
        String query = initialQuery;

        switch(queryType){
            case ID_QUERY_TYPE:
                query += "id=" + placeId;
                break;

            case NAME_QUERY_TYPE:
                query += "q=" + placeName;
                break;

            case LOCATION_QUERY_TYPE:
                query += "lat=" + placeLocation.getLatitude() + "&lon=" + placeLocation.getLongitude();
                break;
        }

        query += "&lang=" + Locale.getDefault().getLanguage();

        return query;
    }

    public static RequestParams getAdditionalParameters(Context context){
        RequestParams params = new RequestParams();
        params.put("x-api-key", getApiKey(context));
        return params;
    }
}

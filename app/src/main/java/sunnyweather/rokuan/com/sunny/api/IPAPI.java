package sunnyweather.rokuan.com.sunny.api;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import sunnyweather.rokuan.com.sunny.data.LocationInfo;
import sunnyweather.rokuan.com.sunny.utils.Utils;

/**
 * Class containing methods to retrieve IP
 */
public class IPAPI {
    public static LocationInfo getLocation(){
        JSONObject json = Utils.getJSON("http://ipinfo.io/json", null);
        LocationInfo info = null;

        try {
            String city = json.getString("city");
            String country = json.getString("country");
            String locationString = json.getString("loc");
            String[] locationFields = locationString.split(",");

            info = new LocationInfo(Double.parseDouble(locationFields[0]), Double.parseDouble(locationFields[1]), city, country);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return info;
    }
}

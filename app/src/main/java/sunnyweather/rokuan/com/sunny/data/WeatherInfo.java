package sunnyweather.rokuan.com.sunny.data;

import android.graphics.Bitmap;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import sunnyweather.rokuan.com.sunny.openweatherapi.OpenWeatherAPI;

/**
 * Created by Christophe on 23/01/2015.
 */
public class WeatherInfo {
    private String place;
    private double temperature;
    private double humidity;
    private double pressure;
    private Date date;
    private Time sunrise;
    private Time sunset;
    private Bitmap weatherImage;
    private String weatherDescription;

    public WeatherInfo(){

    }

    public static WeatherInfo buildFromJSON(JSONObject json) throws JSONException {
        WeatherInfo info = new WeatherInfo();

        JSONObject main = json.getJSONObject("main");
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

        info.date = new Date(json.getLong("dt") * 1000);

        info.temperature = main.getDouble("temp");
        info.pressure = main.getDouble("pressure");
        info.humidity = main.getInt("humidity");

        info.weatherDescription = weather.getString("description");
        info.weatherImage = OpenWeatherAPI.getIcon(weather.getString("icon"));

        try{
            JSONObject sys = json.getJSONObject("sys");
            info.sunrise = new Time();
            info.sunrise.set(sys.getLong("sunrise"));
            info.sunset = new Time();
            info.sunset.set(sys.getLong("sunset"));
        } catch (Exception e){
            Log.e("Sunny - Weather (WInfo JSON)", e.getMessage());
        }

        return info;
    }



    public String getPlace() {
        return place;
    }

    public double getTemperature() {
        // TODO: verifier l'unite de la temperature
        return (temperature - 273.15);
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public Date getDate() {
        return date;
    }

    public Time getSunrise() {
        return sunrise;
    }

    public Time getSunset() {
        return sunset;
    }

    public Bitmap getWeatherImage() {
        return weatherImage;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }
}

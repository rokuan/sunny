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
    private Place place;
    private double temperature;
    private double humidity;
    private double pressure;
    private double speed;
    private Date date;
    private Date sunrise;
    private Date sunset;
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

        info.speed = json.getJSONObject("wind").getInt("speed");

        info.weatherDescription = weather.getString("description");
        info.weatherImage = OpenWeatherAPI.getIcon(weather.getString("icon"));

        try{
            JSONObject sys = json.getJSONObject("sys");
            info.sunrise = new Date(sys.getLong("sunrise") * 1000);
            info.sunset = new Date(sys.getLong("sunset") * 1000);
        } catch (Exception e){
            Log.e("Sunny - Weather (WInfo JSON)", e.getMessage());
        }

        return info;
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

    public Date getSunrise() {
        return sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

    public Bitmap getWeatherImage() {
        return weatherImage;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public double getSpeed() {
        return speed;
    }
}

package sunnyweather.rokuan.com.sunny.api.openweather;

/**
 * Created by LEBEAU Christophe on 27/04/2015.
 */
public interface WeatherDataResultCallback {
    void onWeatherDataResult(boolean success, WeatherData result);
}

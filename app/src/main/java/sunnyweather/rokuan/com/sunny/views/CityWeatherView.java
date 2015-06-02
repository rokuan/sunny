package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.openweather.City;
import sunnyweather.rokuan.com.sunny.api.openweather.OpenWeatherMapAPI;
import sunnyweather.rokuan.com.sunny.api.openweather.WeatherData;
import sunnyweather.rokuan.com.sunny.api.openweather.WeatherDataResultCallback;
import sunnyweather.rokuan.com.sunny.utils.Format;

/**
 * Created by LEBEAU Christophe on 02/06/15.
 */
public class CityWeatherView extends LinearLayout implements WeatherDataResultCallback {
    private static final SimpleDateFormat sunFormat = new SimpleDateFormat("HH:mm");
    //private static final String temperatureFormat = "%.2f";

    private City city;

    @InjectView(R.id.view_weather_frame_loading) protected View loadingFrame;
    @InjectView(R.id.view_weather_frame_no_data) protected View noDataFrame;
    @InjectView(R.id.view_weather_frame_content) protected View weatherFrame;

    @InjectView(R.id.view_weather_place_image) protected ImageView locationImage;
    @InjectView(R.id.view_weather_place_name) protected TextView locationName;
    @InjectView(R.id.view_weather_image) protected ImageView locationWeatherIcon;
    @InjectView(R.id.view_weather_min_temperature) protected TextView locationMinTemperature;
    @InjectView(R.id.view_weather_max_temperature) protected TextView locationMaxTemperature;
    @InjectView(R.id.view_weather_pressure) protected TextView locationPressure;
    @InjectView(R.id.view_weather_wind) protected TextView locationWind;
    @InjectView(R.id.view_weather_sunrise) protected TextView locationSunrise;
    @InjectView(R.id.view_weather_sunset) protected TextView locationSunset;

    /*this.findViewById(R.id.weather_item_data_refresh).setOnClickListener(this);
    this.findViewById(R.id.weather_item_refresh).setOnClickListener(this);*/

    public CityWeatherView(Context context, City c) {
        super(context);
        city = c;

        initCityWeatherView();
    }

    private void initCityWeatherView(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.view_weather, this);

        ButterKnife.inject(this);
        refreshData();
    }

    @OnClick({ R.id.view_weather_data_refresh, R.id.view_weather_refresh })
    public void refreshData(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(OpenWeatherMapAPI.getWeatherForIdURL(city.getId()), OpenWeatherMapAPI.getAdditionalParameters(this.getContext()), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                CityWeatherView.this.startLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                try {
                    CityWeatherView.this.onWeatherDataResult(true, WeatherData.buildFromJSON(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    CityWeatherView.this.onWeatherDataResult(false, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                CityWeatherView.this.onWeatherDataResult(false, null);
            }
        });
    }

    private void renderWeatherData(WeatherData data){
        Picasso.with(this.getContext()).load(OpenWeatherMapAPI.getBitmapURL(data.getIcon())).into(locationWeatherIcon);
        locationName.setText(data.getPlace().getName());
        /*locationMinTemperature.setText(Format.formatTemperature(data.getMinTemperature()));
        locationMaxTemperature.setText(Format.formatTemperature(data.getMaxTemperature()));*/
        locationMinTemperature.setText(String.valueOf(Math.round(data.getMinTemperature())));
        locationMaxTemperature.setText(String.valueOf(Math.round(data.getMaxTemperature())));
        locationPressure.setText(data.getPressure() + " Pa");
        locationWind.setText(data.getSpeed() + " Km/H");
        locationSunrise.setText(sunFormat.format(data.getSunrise()));
        locationSunset.setText(sunFormat.format(data.getSunset()));
    }

    private void startLoading(){
        loadingFrame.setVisibility(VISIBLE);
    }

    private void endLoading(boolean success){
        if(success){
            weatherFrame.setVisibility(VISIBLE);
        } else {
            noDataFrame.setVisibility(VISIBLE);
        }

        loadingFrame.setVisibility(INVISIBLE);
    }

    @Override
    public void onWeatherDataResult(boolean success, WeatherData result) {
        if(success){
            renderWeatherData(result);
        }

        endLoading(success);
    }
}

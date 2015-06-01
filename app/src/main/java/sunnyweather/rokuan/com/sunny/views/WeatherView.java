package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.openweather.OpenWeatherMapAPI;
import sunnyweather.rokuan.com.sunny.api.openweather.WeatherData;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class WeatherView extends LinearLayout {
    private WeatherData data;

    @InjectView(R.id.view_weather_place_name) protected TextView placeName;
    @InjectView(R.id.view_weather_image) protected ImageView weatherImage;
    @InjectView(R.id.view_weather_min_temperature) protected TextView minTemperature;
    @InjectView(R.id.view_weather_max_temperature) protected TextView maxTemperature;
    @InjectView(R.id.view_weather_temperature_unit) protected TextView temperatureUnit;

    public WeatherView(Context context, WeatherData wData) {
        super(context);
        data = wData;

        initWeatherView();
    }

    private void initWeatherView(){
        //this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_weather, this);

        ButterKnife.inject(this);

        placeName.setText(data.getPlace().toString());
        minTemperature.setText(String.valueOf(Math.round(data.getMinTemperature())));
        maxTemperature.setText(String.valueOf(Math.round(data.getMaxTemperature())));
        temperatureUnit.setText("°C");

        Picasso.with(this.getContext()).load(OpenWeatherMapAPI.getBitmapURL(data.getIcon())).into(weatherImage);
        // TODO: ajouter les autres champs
    }
}

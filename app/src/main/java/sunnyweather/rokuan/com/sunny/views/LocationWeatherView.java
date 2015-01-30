package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.Place;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;

/**
 * Created by LEBEAU Christophe on 29/01/2015.
 */
public class LocationWeatherView extends LinearLayout implements View.OnClickListener {
    private Place place;
    private SimpleDateFormat sunFormat = new SimpleDateFormat("HH:mm");

    private View loadingFrame;
    private View noDataFrame;
    private View weatherFrame;

    private TextView locationName;
    private TextView locationTemperature;
    private TextView locationPressure;
    private TextView locationWind;
    private TextView locationSunrise;
    private TextView locationSunset;

    public LocationWeatherView(Context context) {
        super(context);
        initComponent();
    }

    public LocationWeatherView(Context context, Place p){
        this(context);
        this.place = p;
    }

    /*public LocationWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent();
    }*/

    /*public LocationWeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }*/

    private void initComponent(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_item, this);

        loadingFrame = this.findViewById(R.id.weather_item_frame_loading);
        noDataFrame = this.findViewById(R.id.weather_item_frame_no_data);
        weatherFrame = this.findViewById(R.id.weather_item_frame_content);

        this.findViewById(R.id.weather_item_data_refresh).setOnClickListener(this);
        this.findViewById(R.id.weather_item_refresh).setOnClickListener(this);

        locationName = (TextView)this.findViewById(R.id.weather_item_name);
        locationTemperature = (TextView)this.findViewById(R.id.weather_item_temperature);
        locationPressure = (TextView)this.findViewById(R.id.weather_item_pressure);
        locationWind = (TextView)this.findViewById(R.id.weather_item_wind);
        locationSunrise = (TextView)this.findViewById(R.id.weather_item_sunrise);
        locationSunset = (TextView)this.findViewById(R.id.weather_item_sunset);
    }

    private void refreshData(){

    }

    private void startLoading(){
        // TODO:
    }

    private void endLoading(boolean success){
        (success ? noDataFrame : weatherFrame).setVisibility(VISIBLE);
    }

    public void setWeatherContent(WeatherInfo wInfo){
        if(wInfo == null){
            noDataFrame.setVisibility(VISIBLE);
            //loadingFrame.setVisibility(INVISIBLE);
            weatherFrame.setVisibility(INVISIBLE);
        } else {
            noDataFrame.setVisibility(INVISIBLE);
            //loadingFrame.setVisibility(INVISIBLE);

            //locationName.setText(place.getName());
            locationName.setText(wInfo.getPlace().getName());
            locationTemperature.setText(wInfo.getTemperature() + " °C");
            locationPressure.setText(wInfo.getPressure() + " Pa");
            locationWind.setText(wInfo.getSpeed() + "Km/H");
            locationSunrise.setText(sunFormat.format(wInfo.getSunrise()));
            locationSunset.setText(sunFormat.format(wInfo.getSunset()));

            weatherFrame.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.weather_item_data_refresh:
            case R.id.weather_item_refresh:
                refreshData();
                break;
        }
    }
}

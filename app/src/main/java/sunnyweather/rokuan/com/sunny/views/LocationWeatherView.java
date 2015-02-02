package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.WikipediaAPI;
import sunnyweather.rokuan.com.sunny.data.Place;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;
import sunnyweather.rokuan.com.sunny.api.OpenWeatherAPI;
import sunnyweather.rokuan.com.sunny.utils.Format;

/**
 * Created by LEBEAU Christophe on 29/01/2015.
 */
public class LocationWeatherView extends LinearLayout implements View.OnClickListener {
    private Place place;
    private static final SimpleDateFormat sunFormat = new SimpleDateFormat("HH:mm");
    private static final String temperatureFormat = "%.2f";

    private View loadingFrame;
    private View noDataFrame;
    private View weatherFrame;

    private ImageView locationImage;
    private TextView locationName;
    private TextView locationTemperature;
    private TextView locationPressure;
    private TextView locationWind;
    private TextView locationSunrise;
    private TextView locationSunset;

    private WeatherInfo infos;

    private Handler handler;

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
        handler = new Handler();

        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_item, this);

        loadingFrame = this.findViewById(R.id.weather_item_frame_loading);
        noDataFrame = this.findViewById(R.id.weather_item_frame_no_data);
        weatherFrame = this.findViewById(R.id.weather_item_frame_content);

        this.findViewById(R.id.weather_item_data_refresh).setOnClickListener(this);
        this.findViewById(R.id.weather_item_refresh).setOnClickListener(this);

        locationImage = (ImageView)this.findViewById(R.id.weather_item_image);
        locationName = (TextView)this.findViewById(R.id.weather_item_name);
        locationTemperature = (TextView)this.findViewById(R.id.weather_item_temperature);
        locationPressure = (TextView)this.findViewById(R.id.weather_item_pressure);
        locationWind = (TextView)this.findViewById(R.id.weather_item_wind);
        locationSunrise = (TextView)this.findViewById(R.id.weather_item_sunrise);
        locationSunset = (TextView)this.findViewById(R.id.weather_item_sunset);

        // TODO: ajouter le nom de la ville sur loadingFrame/noDataFrame
    }

    public void refreshData(){
        if(place == null){
            return;
        }

        startLoading();
    }

    private void startLoading(){
        // TODO:
        loadingFrame.setVisibility(VISIBLE);

        Thread fetchingThread = new Thread(new Runnable(){
            public void run(){
                try {
                    final WeatherInfo weather = OpenWeatherAPI.getWeather(LocationWeatherView.this.getContext(), place.getId());
                    //final Bitmap placeBitmap = WikipediaAPI.getTitleImage();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setWeatherContent(weather);
                            endLoading(true);
                        }
                    });
                }catch(Exception e){
                    // Timeout exception
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setWeatherContent(null);
                            endLoading(false);
                        }
                    });
                }
            }
        });
        fetchingThread.start();
    }

    private void endLoading(boolean success){
        (success ? weatherFrame : noDataFrame).setVisibility(VISIBLE);
        loadingFrame.setVisibility(INVISIBLE);
    }

    public void setWeatherContent(WeatherInfo wInfo){
        setWeatherContent(wInfo, null);
    }

    private void setWeatherContent(WeatherInfo wInfo, Bitmap bmp){
        if(wInfo == null){
            noDataFrame.setVisibility(VISIBLE);
            //loadingFrame.setVisibility(INVISIBLE);
            weatherFrame.setVisibility(INVISIBLE);
        } else {
            noDataFrame.setVisibility(INVISIBLE);
            //loadingFrame.setVisibility(INVISIBLE);
            //locationName.setText(place.getName());
            if(bmp != null) {
                locationImage.setImageBitmap(bmp);
            }
            locationName.setText(wInfo.getPlace().getName());
            locationTemperature.setText(Format.formatTemperature(wInfo.getTemperature()) + " °C");
            locationPressure.setText(wInfo.getPressure() + " Pa");
            locationWind.setText(wInfo.getSpeed() + "Km/H");
            locationSunrise.setText(sunFormat.format(wInfo.getSunrise()));
            locationSunset.setText(sunFormat.format(wInfo.getSunset()));

            weatherFrame.setVisibility(VISIBLE);
        }

        this.infos = wInfo;
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

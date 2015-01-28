package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;

/**
 * Created by Christophe on 25/01/2015.
 */
public class SimpleWeatherView extends WeatherView {
    private TextView temperature;
    private TextView date;
    private ImageView icon;

    public SimpleWeatherView(Context context) {
        super(context);
        initLayout();
    }

    public SimpleWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    @Override
    public void setWeatherContent(WeatherInfo wInfo) {
        temperature.setText(wInfo.getTemperature() + "°C");
        date.setText(dateFormat.format(wInfo.getDate()));
        icon.setImageBitmap(wInfo.getWeatherImage());
    }

    @Override
    public void setForecastContent(ForecastInfo fInfo) {
        temperature.setText(fInfo.getTemperature() + "°C");
        date.setText(dateFormat.format(fInfo.getDate()));
        icon.setImageBitmap(fInfo.getWeatherImage());
    }

    private void initLayout(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_simple_weather_view, this);

        temperature = (TextView)this.findViewById(R.id.simple_weather_temperature);
        date = (TextView)this.findViewById(R.id.simple_weather_date);
        icon = (ImageView)this.findViewById(R.id.simple_weather_icon);
    }
}

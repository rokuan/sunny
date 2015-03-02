package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;
import sunnyweather.rokuan.com.sunny.utils.Format;

/**
 * A view that displays a small part of the weather data
 */
public class SimpleWeatherView extends WeatherView {
    private ForecastInfo info;

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

    /*@Override
    public void setWeatherContent(WeatherInfo wInfo) {
        temperature.setText(Format.formatTemperature(wInfo.getTemperature()) + "°C");
        date.setText(Format.dateFormat.format(wInfo.getDate()));
        icon.setImageBitmap(wInfo.getWeatherImage());
    }*/

    @Override
    public void setForecastContent(ForecastInfo fInfo) {
        info = fInfo;

        temperature.setText(Format.formatTemperature(fInfo.getTemperature()) + "°C");
        date.setText(Format.dateFormat.format(fInfo.getDate()));
        icon.setImageBitmap(fInfo.getWeatherImage());
    }

    private void initLayout(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_simple_weather_view, this);

        temperature = (TextView)this.findViewById(R.id.simple_weather_temperature);
        date = (TextView)this.findViewById(R.id.simple_weather_date);
        icon = (ImageView)this.findViewById(R.id.simple_weather_icon);
    }

    public ForecastInfo getForecastContent() {
        return info;
    }
}

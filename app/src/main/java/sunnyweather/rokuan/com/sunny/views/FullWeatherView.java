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
 * A view that tends to display an important part of the whole weather data
 */
public class FullWeatherView extends WeatherView {
    private ForecastInfo info;

    private TextView temperature;
    private TextView date;
    private ImageView icon;

    public FullWeatherView(Context context) {
        super(context);
        initLayout();
    }

    public FullWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    /*@TargetApi(11)
    public FullWeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }*/

    /*@Override
    public void setWeatherContent(WeatherInfo wInfo) {
        temperature.setText(Format.formatTemperature(wInfo.getTemperature()) + "°C");
        date.setText(Format.dateFormat.format(wInfo.getDate()));
        icon.setImageBitmap(wInfo.getWeatherImage());
    }*/

    @Override
    public void setForecastContent(ForecastInfo fInfo) {
        info = fInfo;

        temperature.setText(Format.formatTemperature(fInfo.getMinTemperature()) + " ~ " + Format.formatTemperature(fInfo.getMaxTemperature()) + "°C");
        date.setText(Format.dateFormat.format(fInfo.getDate()));
        icon.setImageBitmap(fInfo.getWeatherImage());
    }

    @Override
    public ForecastInfo getForecastContent(){
        return info;
    }

    private void initLayout(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_full_weather_view, this);

        temperature = (TextView)this.findViewById(R.id.full_weather_temperature);
        date = (TextView)this.findViewById(R.id.full_weather_date);
        icon = (ImageView)this.findViewById(R.id.full_weather_icon);
    }
}

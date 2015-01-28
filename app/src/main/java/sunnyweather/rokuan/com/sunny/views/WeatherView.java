package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;

import sunnyweather.rokuan.com.sunny.data.ForecastInfo;
import sunnyweather.rokuan.com.sunny.data.WeatherInfo;

/**
 * Created by Christophe on 23/01/2015.
 */
public abstract class WeatherView extends RelativeLayout {
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");

    public WeatherView(Context context) {
        super(context);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }*/

    public abstract void setWeatherContent(WeatherInfo wInfo);
    public abstract void setForecastContent(ForecastInfo fInfo);
}

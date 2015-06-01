package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.widget.LinearLayout;

import sunnyweather.rokuan.com.sunny.api.openweather.City;

/**
 * Created by LEBEAU Christophe on 02/06/15.
 */
public class CityWeatherView extends LinearLayout {
    private City city;

    public CityWeatherView(Context context, City c) {
        super(context);
        city = c;
    }
}

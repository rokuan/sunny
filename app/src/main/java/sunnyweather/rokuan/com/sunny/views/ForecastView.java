package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.data.ForecastData;

/**
 * Created by LEBEAU Christophe on 01/06/15.
 */
public class ForecastView extends LinearLayout {
    private ForecastData data;

    public ForecastView(Context context, ForecastData d) {
        super(context);
        data = d;

        initForecastView();
    }

    private void initForecastView(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        inflater.inflate(R.layout.view_forecast, this);
    }
}

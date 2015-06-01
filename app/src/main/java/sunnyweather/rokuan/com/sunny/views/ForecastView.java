package sunnyweather.rokuan.com.sunny.views;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.openweather.ForecastData;
import sunnyweather.rokuan.com.sunny.api.openweather.OpenWeatherMapAPI;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class ForecastView extends LinearLayout {
    private ForecastData data;

    private SingleWeatherDataAdapter adapter;

    @InjectView(R.id.view_forecast_place) protected TextView placeName;
    @InjectView(R.id.view_forecast_list) protected ListView listView;

    public ForecastView(Context context, ForecastData fData) {
        super(context);
        data = fData;
        initForecastView();
    }

    private void initForecastView() {
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_forecast, this);

        ButterKnife.inject(this);

        adapter = new SingleWeatherDataAdapter(this.getContext(), data.getForecast());

        placeName.setText(data.getCity().toString());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SingleWeatherDataDialog.newInstance(adapter.getItem(i)).show(
                        ((ActionBarActivity)ForecastView.this.getContext()).getSupportFragmentManager(), "FORECAST_DLG");
            }
        });
    }

    class SingleWeatherDataAdapter extends ArrayAdapter<ForecastData.SingleWeatherData> {
        private LayoutInflater inflater;

        public SingleWeatherDataAdapter(Context context, List<ForecastData.SingleWeatherData> objects) {
            super(context, R.layout.view_forecast_item, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.view_forecast_item, parent, false);
            }

            ForecastData.SingleWeatherData item = this.getItem(position);

            TextView date = (TextView)v.findViewById(R.id.view_forecast_item_date);
            ImageView icon = (ImageView)v.findViewById(R.id.view_forecast_item_image);
            //TextView temperature = (TextView)v.findViewById(R.id.view_forecast_item_temperature);
            TextView minTemperature = (TextView)v.findViewById(R.id.view_forecast_item_min_temperature);
            TextView maxTemperature = (TextView)v.findViewById(R.id.view_forecast_item_max_temperature);

            date.setText(new SimpleDateFormat("EEE dd MMMMM").format(item.getDate()));
            //icon.setImageBitmap(item.getWeatherImage());
            Picasso.with(this.getContext()).load(OpenWeatherMapAPI.getBitmapURL(item.getWeatherIconName())).into(icon);
            //temperature.setText(Math.round(item.getTemperature()) + "°C");
            // TODO: verifier l'unite de la temperature

            minTemperature.setText(Math.round(item.getMinTemperature()) + "°C");
            maxTemperature.setText(Math.round(item.getMaxTemperature()) + "°C");

            return v;
        }
    }
}

package sunnyweather.rokuan.com.sunny.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sunnyweather.rokuan.com.sunny.R;
import sunnyweather.rokuan.com.sunny.api.openweather.ForecastData;
import sunnyweather.rokuan.com.sunny.api.openweather.OpenWeatherMapAPI;
import sunnyweather.rokuan.com.sunny.utils.Format;

/**
 * Created by LEBEAU Christophe on 01/06/15.
 */
public class SingleWeatherDataDialog extends DialogFragment {
    private ForecastData.SingleWeatherData info;

    @InjectView(R.id.forecast_dialog_title) protected TextView title;
    @InjectView(R.id.forecast_dialog_icon) protected ImageView icon;
    @InjectView(R.id.forecast_dialog_description) protected TextView description;
    //@InjectView(R.id.forecast_dialog_temperature) protected TextView temperature;
    @InjectView(R.id.forecast_dialog_min_temperature) protected TextView minTemperature;
    @InjectView(R.id.forecast_dialog_max_temperature) protected TextView maxTemperature;
    @InjectView(R.id.forecast_dialog_pressure) protected TextView pressure;
    @InjectView(R.id.forecast_dialog_humidity) protected TextView humidity;
    @InjectView(R.id.forecast_dialog_wind) protected TextView wind;

    public static SingleWeatherDataDialog newInstance(ForecastData.SingleWeatherData info){
        SingleWeatherDataDialog dialog = new SingleWeatherDataDialog();
        dialog.info = info;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.forecast_dialog, null);

        ButterKnife.inject(this, content);

        ImageButton close = (ImageButton)content.findViewById(R.id.forecast_dialog_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleWeatherDataDialog.this.dismiss();
            }
        });

            /*TextView title = (TextView)content.findViewById(R.id.forecast_dialog_title);
            ImageView icon = (ImageView)content.findViewById(R.id.forecast_dialog_icon);
            TextView description = (TextView)content.findViewById(R.id.forecast_dialog_description);
            TextView temperature = (TextView)content.findViewById(R.id.forecast_dialog_temperature);
            TextView pressure = (TextView)content.findViewById(R.id.forecast_dialog_pressure);
            TextView humidity = (TextView)content.findViewById(R.id.forecast_dialog_humidity);
            TextView wind = (TextView)content.findViewById(R.id.forecast_dialog_wind);*/

        title.setText(Format.dateFormat.format(info.getDate()));
        Picasso.with(this.getActivity()).load(OpenWeatherMapAPI.getBitmapURL(info.getWeatherIconName())).into(icon);
        description.setText(info.getWeatherDescription());
        //temperature.setText(String.valueOf(Math.round(info.getMaxTemperature())));
        minTemperature.setText(String.valueOf(Math.round(info.getMinTemperature())) + "°C");
        maxTemperature.setText(String.valueOf(Math.round(info.getMaxTemperature())) + "°C");
        pressure.setText(info.getPressure() + " Pa");
        humidity.setText(info.getHumidity() + "%");
        wind.setText(info.getSpeed() + " km/h");

        builder.setView(content);

        return builder.create();
    }
}

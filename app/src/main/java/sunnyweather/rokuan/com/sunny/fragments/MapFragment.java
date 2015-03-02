package sunnyweather.rokuan.com.sunny.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunnyweather.rokuan.com.sunny.R;

/**
 * A fragment that displays weather data about a whole country
 */
public class MapFragment extends SunnyFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void refresh() {
        // TODO:
    }
}

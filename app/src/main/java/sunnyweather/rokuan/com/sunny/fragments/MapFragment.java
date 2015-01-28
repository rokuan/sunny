package sunnyweather.rokuan.com.sunny.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunnyweather.rokuan.com.sunny.R;

/**
 * Created by Christophe on 27/01/2015.
 */
public class MapFragment extends SunnyFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void refresh() {

    }
}

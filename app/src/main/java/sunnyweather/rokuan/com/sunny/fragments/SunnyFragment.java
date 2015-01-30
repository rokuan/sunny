package sunnyweather.rokuan.com.sunny.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import sunnyweather.rokuan.com.sunny.activities.HomeActivity;

/**
 * Created by Christophe on 27/01/2015.
 */
public abstract class SunnyFragment extends Fragment {
    public static final int HOME_FRAGMENT = 1;
    public static final int LOCATION_FRAGMENT = 2;
    public static final int MAP_FRAGMENT = 3;

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SunnyFragment newInstance(int sectionNumber) {
        SunnyFragment fragment;
        Bundle args = new Bundle();

        switch(sectionNumber){
            case HOME_FRAGMENT:
                fragment = new HomeFragment();
                break;
            case LOCATION_FRAGMENT:
                fragment = new MyLocationsFragment();
                break;
            case MAP_FRAGMENT:
            default:
                fragment = new MapFragment();
                break;
        }

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public abstract void refresh();
}

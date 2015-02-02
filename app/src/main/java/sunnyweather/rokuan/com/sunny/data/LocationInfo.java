package sunnyweather.rokuan.com.sunny.data;

import android.location.Location;

/**
 * Created by LEBEAU Christophe on 01/02/2015.
 */
public class LocationInfo {
    private Location location;
    private String city;
    private String country;

    public LocationInfo(double latitude, double longitude, String cityName, String countryCode){
        city = cityName;
        country = countryCode;

        location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Location getLocation(){
        return location;
    }

    public String getPlaceCode(){
        return city + "," + country;
    }
}

package sunnyweather.rokuan.com.sunny.api.openweather;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LEBEAU Christophe on 16/04/2015.
 */
public class City {
    private Long id;
    private String name;
    private String country;

    public City(long cityId, String cityName, String cityCountry){
        id = cityId;
        name = cityName;
        country = cityCountry;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry(){
        return country;
    }

    public static City buildFromJSON(JSONObject json) throws JSONException {
        City c = new City(json.getLong("id"), json.getString("name"), json.getJSONObject("sys").getString("country"));
        return c;
    }

    public static City buildFromCursor(Cursor result) {
        long cityId = result.getLong(0);
        String cityName = result.getString(1);
        String cityCountry = result.getString(2);

        City c = new City(cityId, cityName, cityCountry);
        return c;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return (o instanceof City) && (((City)o).id == this.id);
    }

    @Override
    public String toString(){
        return this.name + "," + this.country;
    }
}

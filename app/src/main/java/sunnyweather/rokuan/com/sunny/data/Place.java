package sunnyweather.rokuan.com.sunny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Christophe on 24/01/2015.
 */
public class Place {
    private Long id;
    private String name;

    public static Place buildFromJSON(JSONObject json) throws JSONException {
        Place place = new Place();

        place.setId(json.getLong("id"));
        place.setName(json.getString("name") + ", " + json.getJSONObject("sys").getString("country"));

        return place;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

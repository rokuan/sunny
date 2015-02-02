package sunnyweather.rokuan.com.sunny.api;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import sunnyweather.rokuan.com.sunny.utils.Utils;

/**
 * Created by LEBEAU Christophe on 31/01/2015.
 */
public class WikipediaAPI {
    private static final String WIKIPEDIA_API_ADDRESS = "http://en.wikipedia.org/w/api.php?action=query&titles=%s&prop=pageimages&format=json&pithumbsize=%d";

    private static final int DEFAULT_IMAGE_WIDTH = 480;

    public static Bitmap getTitleImage(String title){
        return getTitleImage(title, DEFAULT_IMAGE_WIDTH);
    }

    public static Bitmap getTitleImage(String title, int width){
        try {
            JSONObject result = Utils.getJSON(String.format(WIKIPEDIA_API_ADDRESS, title, width), null);
            JSONObject pages = result.getJSONObject("query").getJSONObject("pages");
            Iterator<String> pagesKeys = pages.keys();

            if(pages.keys().hasNext()){
                String imageAddress = pages.getJSONObject(pagesKeys.next()).getJSONObject("thumbnail").getString("source");
                return Utils.getBitmapFromURL(imageAddress);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
}

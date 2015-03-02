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
 * Class with methods to get data from the Wikipedia API
 */
public class WikipediaAPI {
    private static final String WIKIPEDIA_API_ADDRESS = "http://en.wikipedia.org/w/api.php?action=query&titles=%s&prop=pageimages&format=json&pithumbsize=%d";

    private static final int DEFAULT_IMAGE_WIDTH = 480;

    /**
     * Fetches an image for a given item title
     * @param title the title
     * @return a bitmap of the image, null when none or if an error occurred
     */
    public static Bitmap getTitleImage(String title){
        return getTitleImage(title, DEFAULT_IMAGE_WIDTH);
    }

    /**
     * Fetches an image for a given item name
     * @param title the title
     * @param width the image width
     * @return a bitmap of the image, null if an error occurred or none was found
     */
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

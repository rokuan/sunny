package sunnyweather.rokuan.com.sunny.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by LEBEAU Christophe on 29/01/2015.
 */
public class Utils {
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    /**
     * Checks whether or not the network is available
     * @param context the android context to be used
     * @return true if network is currently available, false otherwise
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Converts an address pointing to an image into a Bitmap
     * @param address the address leading to the image
     * @return a bitmap obtained from the address or null if an error occurred
     */
    public static Bitmap getBitmapFromURL(String address){
        Bitmap image = null;

        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            image = BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Gets a JSONObject built from the data retrieved from the HTTP address
     * @param address the HTTP address
     * @param properties the request properties to be added
     * @return a JSONObject build from the result
     */
    public static JSONObject getJSON(String address, ContentValues properties){
        HttpURLConnection connection = null;
        URL url;

        try {
            url = new URL(address);
            connection = (HttpURLConnection)url.openConnection();

            if(properties != null) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    for (Map.Entry<String, Object> item : properties.valueSet()) {
                        connection.addRequestProperty(item.getKey(), item.getValue().toString());
                    }
                }
            }

            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder json = new StringBuilder();
            char[] buffer = new char[1024];
            int read;

            while((read = reader.read(buffer, 0, buffer.length)) != -1){
                json.append(buffer, 0, read);
            }

            reader.close();

            //System.out.println(json.toString());

            return new JSONObject(json.toString());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }
    }
}

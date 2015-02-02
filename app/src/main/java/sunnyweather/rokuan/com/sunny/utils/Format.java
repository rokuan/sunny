package sunnyweather.rokuan.com.sunny.utils;

import java.text.SimpleDateFormat;

/**
 * Created by LEBEAU Christophe on 31/01/2015.
 */
public class Format {
    /*enum Temperature {
        KELVIN,
        CELSIUS
    }*/

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");

    public static String formatTemperature(double temperature){
        return String.format("%.2f", temperature);
    }
}

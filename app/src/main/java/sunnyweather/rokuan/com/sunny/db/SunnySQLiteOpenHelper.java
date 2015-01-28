package sunnyweather.rokuan.com.sunny.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Christophe on 24/01/2015.
 */
public class SunnySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sunny";
    private static final int DB_VERSION = 1;

    private static final int CITIES = 0;

    private static final String[] tables = new String[] {
            "city"
    };

    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_ORDER = "city_order";

    private static final String CITY_QUERY = "CREATE TABLE " + tables[CITIES] + "(" +
            CITY_ID + " INTEGER PRIMARY KEY, " +
            CITY_NAME + " TEXT UNIQUE NOT NULL, " +
            CITY_ORDER + " INTEGER UNIQUE NOT NULL" +
            ")";

    public SunnySQLiteOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CITY_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //for(int i=0; i<tables.length; i++) {
        for(String tableName: tables){
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

        this.onCreate(db);
    }
}

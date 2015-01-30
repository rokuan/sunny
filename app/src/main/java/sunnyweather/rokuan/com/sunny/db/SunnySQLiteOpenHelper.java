package sunnyweather.rokuan.com.sunny.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.database.DatabaseUtilsCompat;

import java.util.ArrayList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.data.Place;

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

    public void addCity(Place place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CITY_ID, place.getId());
        values.put(CITY_NAME, place.getName());
        // TOCHECK: quand on atteint le nombre max de locations
        values.put(CITY_ORDER, (int)DatabaseUtils.queryNumEntries(db, tables[CITIES]));

        db.insert(tables[CITIES], null, values);
        db.close();
    }

    public void insertCity(int position, Long cityId){
        SQLiteDatabase db = this.getWritableDatabase();

        db.rawQuery("UPDATE " + tables[CITIES] + " SET " + CITY_ORDER + " = " + CITY_ORDER + " + 1 WHERE " + CITY_ORDER + " >= " + position, null);

        ContentValues updateValues = new ContentValues();

        updateValues.put(CITY_ORDER, position);
        db.update(tables[CITIES], updateValues, CITY_ID + " = ?", new String[]{ cityId.toString() });

        db.close();
    }

    public boolean cityExists(Long cityId){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;
        Cursor results = db.query(tables[CITIES], null, CITY_ID + " = ?", new String[]{ cityId.toString() }, null, null, null);

        exists = (results.getCount() > 0);

        db.close();
        results.close();
        return exists;
    }

    public List<Place> queryAllCities(){
        List<Place> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(tables[CITIES], null, null, null, null, null, CITY_ORDER + " ASC");

        if(results.getCount() > 0){
            places = new ArrayList<>(results.getCount());
            results.moveToFirst();

            while(!results.isAfterLast()){
                places.add(Place.buildFromCursor(results));
                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return places;
    }

    public List<Place> queryCities(String name){
        List<Place> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(tables[CITIES], null, CITY_NAME + " LIKE %?%", new String[]{ name }, null, null, CITY_ORDER + " ASC");

        if(results.getCount() > 0){
            places = new ArrayList<>(results.getCount());
            results.moveToFirst();

            while(!results.isAfterLast()){
                places.add(Place.buildFromCursor(results));
                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return places;
    }
}

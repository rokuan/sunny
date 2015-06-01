package sunnyweather.rokuan.com.sunny.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import sunnyweather.rokuan.com.sunny.api.openweather.City;

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
    public static final String CITY_COUNTRY = "city_country";
    public static final String CITY_ORDER = "city_order";

    private static final String CITY_QUERY = "CREATE TABLE " + tables[CITIES] + "(" +
            CITY_ID + " INTEGER PRIMARY KEY, " +
            CITY_NAME + " TEXT UNIQUE NOT NULL, " +
            CITY_COUNTRY + " VARCHAR(2) NOT NULL, " +
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

    /**
     * Adds a new place to the database
     * @param place the place to be added
     */
    public void addCity(City place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CITY_ID, place.getId());
        values.put(CITY_NAME, place.getName());
        values.put(CITY_COUNTRY, place.getCountry());
        // TOCHECK: quand on atteint le nombre max de locations
        values.put(CITY_ORDER, (int)DatabaseUtils.queryNumEntries(db, tables[CITIES]));

        db.insert(tables[CITIES], null, values);
        db.close();
    }

    /**
     * Inserts a city in the database at the given position
     * @param position the position where to insert the city
     * @param cityId
     */
    public void insertCity(int position, Long cityId){
        SQLiteDatabase db = this.getWritableDatabase();

        db.rawQuery("UPDATE " + tables[CITIES] + " SET " + CITY_ORDER + " = " + CITY_ORDER + " + 1 WHERE " + CITY_ORDER + " >= " + position, null);

        ContentValues updateValues = new ContentValues();

        updateValues.put(CITY_ORDER, position);
        db.update(tables[CITIES], updateValues, CITY_ID + " = ?", new String[]{ cityId.toString() });

        db.close();
    }

    /**
     * Deletes an existing city
     * @param cityId the city id
     */
    public void deleteCity(Long cityId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tables[CITIES], CITY_ID + " = ?", new String[]{ cityId.toString() });
        db.close();
    }

    /**
     * Tells whether or not a city whose ID is {@code cityId} does exist into the database
     * @param cityId the city id
     * @return true if the city does exist, false otherwise
     */
    public boolean cityExists(Long cityId){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists;
        Cursor results = db.query(tables[CITIES], null, CITY_ID + " = ?", new String[]{ cityId.toString() }, null, null, null);

        exists = (results.getCount() > 0);

        db.close();
        results.close();
        return exists;
    }

    /**
     * Returns all the cities the user has added into the database
     * @return a list of all the cities the user has marked as favorites
     */
    public List<City> queryAllCities(){
        List<City> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(tables[CITIES], null, null, null, null, null, CITY_ORDER + " ASC");

        if(results.moveToFirst()){
            places = new ArrayList<>(results.getCount());

            while(!results.isAfterLast()){
                places.add(City.buildFromCursor(results));
                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return places;
    }

    /**
     * Queries all cities whose name contains {@code name}
     * @param name the char sequence to match
     * @return
     */
    public List<City> queryCities(String name){
        List<City> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(tables[CITIES], null, CITY_NAME + " LIKE %?%", new String[]{ name }, null, null, CITY_ORDER + " ASC");

        if(results.moveToFirst()){
            places = new ArrayList<>(results.getCount());

            while(!results.isAfterLast()){
                places.add(City.buildFromCursor(results));
                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return places;
    }
}

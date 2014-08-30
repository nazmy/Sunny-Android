package com.android.nazmy.sunny.app.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;


import com.android.nazmy.sunny.app.data.WeatherContract.LocationEntry;
import com.android.nazmy.sunny.app.data.WeatherContract.WeatherEntry;

import com.android.nazmy.sunny.app.data.WeatherDbHelper;


import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by nazmy on 8/20/14.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb .class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public static final String testName = "San Fransisco";
    ContentValues getLocationContentValues() {
        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        //create content values to hold the mapping between column and values
        ContentValues contentValues = new ContentValues();
        contentValues.put(LocationEntry.COLUMN_CITY_NAME, testName);
        contentValues.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        contentValues.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        contentValues.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return contentValues;
    }

    ContentValues getWeatherContentValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    public void validateCursor(Cursor valueCursor,ContentValues expectedValues) {
        Set<Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

    }

    public void testInsertDb() throws  Throwable {

        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();

        //insert the content values to the table
        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, getLocationContentValues());

        //Verify we got a newly added row
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row ID: " + locationRowId);

       Cursor locationCursor = db.query(LocationEntry.TABLE_NAME, //table to query
                null, //columns to query
                null, //columns for the "where" clause
                null, //values for the "where" clause
                null, //columns to group by
                null, //columns to filter by row group
                null //sort order
                 );

        if (locationCursor.moveToFirst()) {
            validateCursor(locationCursor,getLocationContentValues());
        } else {
            fail("No values returned");
        }


        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, getWeatherContentValues(locationRowId));
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        if (weatherCursor.moveToFirst()) {
            validateCursor(weatherCursor, getWeatherContentValues(locationRowId));
        } else {
            fail("No weather data returned!");
        }
        weatherCursor.close();

        db.close();
    }
}

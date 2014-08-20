package com.android.nazmy.sunny.app.test;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.android.nazmy.sunny.app.data.WeatherDbHelper;

/**
 * Created by nazmy on 8/20/14.
 */
public class TestDb extends AndroidTestCase {
    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }
}

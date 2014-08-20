package com.android.nazmy.sunny.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
          startActivity(new Intent(this,SettingsActivity.class));
        }
        if (id == R.id.action_map) {
            showUserPreferredLocation();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserPreferredLocation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String geo = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_defaultValue));

        Uri uri = new Uri.Builder().appendPath("geo:0,0?q=").appendPath(geo).build();
        Uri geoLocation = uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", geo).build();

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(geoLocation);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }


}

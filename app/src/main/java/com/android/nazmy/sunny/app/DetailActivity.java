package com.android.nazmy.sunny.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.android.nazmy.sunny.app.data.WeatherContract;
import com.android.nazmy.sunny.app.data.WeatherProvider;

import java.util.Date;

public class DetailActivity extends ActionBarActivity {

    private static final int DETAIL_LOADER = 0;
    private static final String DATE_KEY = "date";
    private static final String LOCATION_KEY = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        private ActionProvider mShareActionProvider;
        private String mForecastStr;
        private String mLocation;

        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };

        String weatherDate;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            this.getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            if (null != savedInstanceState) {
                mLocation = savedInstanceState.getString(LOCATION_KEY);
            }
            super.onActivityCreated(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                 weatherDate = intent.getStringExtra(Intent.EXTRA_TEXT);

            }
            return rootView;
        }


        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mForecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (null != mLocation) {
                outState.putString(LOCATION_KEY, mLocation);
            }

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.detailfragment, menu);
            MenuItem menuItem = menu.findItem(R.id.menu_item_share);

            ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null");
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.menu_item_share) {
                Intent shareIntent = createShareForecastIntent();
                startActivity(shareIntent);
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // fragment only uses one loader, so we don't care about checking the id.

            // To only show current and future dates, get the String representation for today,
            // and filter the query to return weather only for dates after or including today.
            // Only return data after today.


            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

            mLocation = Utility.getPreferredLocation(getActivity());
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    mLocation, weatherDate);
            Log.v(LOG_TAG, weatherForLocationUri.toString());


            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToFirst()) {
                boolean isMetric = Utility.isMetric(getActivity());
                String dateText = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
                String weatherDesc = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
                double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
                double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));

                TextView dateView = (TextView) getView().findViewById(R.id.detail_item_date);
                TextView descView = (TextView) getView().findViewById(R.id.detail_item_forecast);
                TextView highView = (TextView) getView().findViewById(R.id.detail_item_high);
                TextView lowView = (TextView) getView().findViewById(R.id.detail_item_low);

                dateView.setText(Utility.formatDate(dateText));
                descView.setText(weatherDesc);
                highView.setText(Utility.formatTemperature(high, isMetric));
                lowView.setText(Utility.formatTemperature(low, isMetric));

                mForecastStr = String.format("%s - %s - %s/%s", dateView.getText(),
                        descView.getText(), highView.getText(), lowView.getText());

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        @Override
        public void onResume() {
            super.onResume();
            if (null != mLocation && mLocation != Utility.getPreferredLocation(getActivity())) {
                this.getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            }
        }
    }
}

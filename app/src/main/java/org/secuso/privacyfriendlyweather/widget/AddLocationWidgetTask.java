package org.secuso.privacyfriendlyweather.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;

import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;

public class AddLocationWidgetTask extends AsyncTask<Object, Void, Object[]> {

    private Context context;

    AddLocationWidgetTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object[] doInBackground(Object... params) {

        City selectedCity = (City) params[0];

        PFASQLiteHelper database = PFASQLiteHelper.getInstance(context);
        boolean isAdded = database.isCityWatched(selectedCity.getCityId());

        if (!isAdded) {
            CityToWatch newCity = new CityToWatch();
            newCity.setCityId(selectedCity.getCityId());
            newCity.setRank(42);
            database.addCityToWatch(newCity);
        }
        database.close();
        return params;
    }

    @Override
    protected void onPostExecute(Object... params) {
        super.onPostExecute(params);
        int ID = (int) params[1];
        int type = (int) params[2];
        Intent intent = new Intent(context, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_WIDGET_ACTION);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ID);
        intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        intent.putExtra("widget_type", type);
        context.startService(intent);
    }
}

package org.secuso.privacyfriendlyweather.widget;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;

import static androidx.core.app.JobIntentService.enqueueWork;
import static org.secuso.privacyfriendlyweather.services.UpdateDataService.SKIP_UPDATE_INTERVAL;

public class AddLocationWidgetTask extends AsyncTask<Object, Void, Object[]> {

    private Context context;

    AddLocationWidgetTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object[] doInBackground(Object... params) {

        City selectedCity = (City) params[0];

        AppDatabase database = AppDatabase.getInstance(context);
        boolean isAdded = database.cityToWatchDao().isCityWatched(selectedCity.getCityId());

        if (!isAdded) {
            CityToWatch newCity = new CityToWatch();
            newCity.setCityId(selectedCity.getCityId());
            newCity.setRank(database.cityToWatchDao().getMaxRank() + 1);
            newCity.setCityName(selectedCity.getCityName());
            newCity.setLongitude(selectedCity.getLongitude());
            newCity.setLatitude(selectedCity.getLatitude());
            database.cityToWatchDao().addCityToWatch(newCity);
        }
        database.close();
        return params;
    }

    @Override
    protected void onPostExecute(Object... params) {
        super.onPostExecute(params);
        int cityID = ((City) params[0]).getCityId();
        Intent intent = new Intent(context, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_SINGLE_ACTION);
        intent.putExtra(SKIP_UPDATE_INTERVAL, true);
        intent.putExtra("cityId", cityID);
        enqueueWork(context, UpdateDataService.class, 0, intent);

    }
}

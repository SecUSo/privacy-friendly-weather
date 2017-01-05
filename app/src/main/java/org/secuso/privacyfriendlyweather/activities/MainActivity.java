package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.AddLocationDialog;
import org.secuso.privacyfriendlyweather.PrefManager;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.services.FetchForecastDataService;
import org.secuso.privacyfriendlyweather.ui.RecycleList.RecyclerItemClickListener;
import org.secuso.privacyfriendlyweather.ui.RecycleList.RecyclerOverviewListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends BaseActivity {

    private final String DEBUG_TAG = "main_activity_debug";
    private PFASQLiteHelper database;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);

        database = PFASQLiteHelper.getInstance(this);

        //TODO Get from DB
        //List<CityToWatch> cities = database.getAllCitiesToWatch();
        List<CityToWatch> cities = new ArrayList<CityToWatch>();

        //TODO Remove on cleanup
        CityToWatch kl = new CityToWatch(2, "", "DE", 1, 2894003, "Kaiserslautern");
        CityToWatch riga = new CityToWatch(1, "", "LV", 2, 456172, "Riga");
        CityToWatch tokyo = new CityToWatch(3, "", "JP", 3, 1850147, "Tokyo");

        cities.add(kl);
        cities.add(riga);
        cities.add(tokyo);

        Collections.sort(cities, new Comparator<CityToWatch>() {
            @Override
            public int compare(CityToWatch o1, CityToWatch o2) {
                return o1.getRank() - o2.getRank();
            }
        });

        prefManager = new PrefManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view_cities);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Get the corresponding city_id of the entry and pass it on to the started activity
                        int cityId = RecyclerOverviewListAdapter.getListItems().get(position).getCityId();
                        Intent intent = new Intent(getBaseContext(), ForecastCityActivity.class);
                        intent.putExtra("cityId", cityId);

                        startFetchingService(cityId);

                        startActivity(intent);
                    }

                    public void onLongItemClick(View view, int position) {
                        //sets the current city as default location
                        setDefaultLocation(RecyclerOverviewListAdapter.getListItems().get(position).getCityId());
                    }

                })
        );

        RecyclerOverviewListAdapter adapter = new RecyclerOverviewListAdapter(getBaseContext(), cities);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.fabAddLocation);
        if (addFab != null) {

            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    AddLocationDialog addMetaDataDialog = new AddLocationDialog();
                    addMetaDataDialog.show(fragmentManager, "AddLocationDialog");
                }
            });

        }

    }

    public void setDefaultLocation(int cityId){
        prefManager.setDefaultLocation(cityId);
        Toast toast = Toast.makeText(getBaseContext(), "XY set as default location", Toast.LENGTH_SHORT);
        toast.show();
        //TODO Is there a better nicer solution?
        recreate();
    }

    public void startFetchingService(int cityId) {
        // Start a background task to retrieve and store the weather forecast data
        Intent forecastIntent = new Intent(this, FetchForecastDataService.class);
        forecastIntent.putExtra("cityId", cityId);
        startService(forecastIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_manage;
    }

}

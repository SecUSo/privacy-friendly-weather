package org.secuso.privacyfriendlyweather.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.secuso.privacyfriendlyweather.AddLocationDialog;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.ui.RecycleList.RecyclerOverviewListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private final String DEBUG_TAG = "main_activity_debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);

        //TODO Get from DB
        List<CityToWatch> cities = new ArrayList<CityToWatch>();

        CityToWatch kl = new CityToWatch(1, "", "DE", 1, 2894003, "Kaiserslautern");
        CityToWatch riga = new CityToWatch(2, "", "LV", 2, 456172, "Riga");
        CityToWatch tokyo = new CityToWatch(3, "", "JP", 3, 1850147, "Tokyo");

        cities.add(kl);
        cities.add(riga);
        cities.add(tokyo);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view_cities);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
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

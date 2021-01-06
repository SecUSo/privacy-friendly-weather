package org.secuso.privacyfriendlyweather.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;
import org.secuso.privacyfriendlyweather.ui.util.AutoCompleteCityTextViewGenerator;
import org.secuso.privacyfriendlyweather.ui.util.MyConsumer;

import static androidx.core.app.JobIntentService.enqueueWork;

/**
 * Class structure taken from tutorial at http://www.androidhive.info/2016/05/android-build-intro-slider-app/
 *
 * @author Karola Marky
 * @version 20161214
 */

public class TutorialActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    AppDatabase database;
    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteCityTextViewGenerator cityTextViewGenerator;
    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);

        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.tutorial_slide1,
                R.layout.tutorial_slide2,
                R.layout.tutorial_slide3,
                R.layout.tutorial_slide4,
                R.layout.tutorial_slide_firstlocation};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(layouts.length - 1);
                //launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    if (selectedCity == null) {
                        Toast.makeText(TutorialActivity.this, R.string.dialog_add_no_city_found, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    launchHomeScreen();
                }
            }
        });

        database = AppDatabase.getInstance(this);
        cityTextViewGenerator = new AutoCompleteCityTextViewGenerator(this, database);
    }

    private void performDone() {
        if (selectedCity == null) {
            cityTextViewGenerator.getCityFromText(true);
            if (selectedCity == null) {
                Toast.makeText(getBaseContext(), "Please choose a location", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        launchHomeScreen();
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        if (selectedCity != null && database != null && !database.cityToWatchDao().isCityWatched(selectedCity.getCityId())) {
            addCity();
        }
        startActivity(new Intent(TutorialActivity.this, ForecastCityActivity.class));
        // getWeatherData(); //not needed, will be done for selected city when ForecastCityActivity is started. Otherwise it will be done twice
        finish();
    }

    private void getWeatherData() {
        // Start a background task to retrieve and store the weather data
        if (selectedCity != null) {
            Intent updateService = new Intent(this, UpdateDataService.class);
            updateService.setAction(UpdateDataService.UPDATE_ALL_ACTION);
            updateService.putExtra(UpdateDataService.CITY_ID, selectedCity.getCityId());
            enqueueWork(this, UpdateDataService.class, 0, updateService);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewPagerPageChangeListener.onPageSelected(viewPager.getCurrentItem());
    }

    public void addCity() {
        if (selectedCity != null) {
            database.cityToWatchDao().addCityToWatch(new CityToWatch(
                    0,
                    selectedCity.getCountryCode(),
                    -1,
                    selectedCity.getCityId(),
                    selectedCity.getCityName(),
                    selectedCity.getLongitude(),
                    selectedCity.getLatitude()
            ));
            /*  TODO: Remove, not needed, will be done in ForecastCityActivity
            Intent intent = new Intent(getApplicationContext(), UpdateDataService.class);
            intent.setAction(UpdateDataService.UPDATE_FORECAST_ACTION);  //includes also current weather via one call API
            enqueueWork(getApplicationContext(), UpdateDataService.class, 0, intent);
             */
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.okay));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View view = layoutInflater.inflate(layouts[position], container, false);
            if (position == layouts.length - 2) {
                TextView link = view.findViewById(R.id.tutorial_owm_link);
                link.setMovementMethod(LinkMovementMethod.getInstance());
                link.setLinkTextColor(Color.CYAN);
                EditText keyInputField = view.findViewById(R.id.tutorial_owm_key_field);
                keyInputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            insertOWMKey();
                        }
                    }
                });
            }
            if (position == dots.length - 1) {
                final WebView webview = view.findViewById(R.id.webViewFirstLocation);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setBackgroundColor(0x00000000);
                webview.setBackgroundResource(R.drawable.map_back);
                //the below is to fix the webview layout if it is loaded to slow (fix for webview in layout with wrap_content height)
                final LinearLayout ll = view.findViewById(R.id.firstlocation_linear_layout);
                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        ll.requestLayout();
                    }
                });
                autoCompleteTextView = view.findViewById(R.id.autoCompleteTvAddFirstStart);
                cityTextViewGenerator.generate(autoCompleteTextView, 100, EditorInfo.IME_ACTION_DONE, new MyConsumer<City>() {
                    @Override
                    public void accept(City city) {
                        selectedCity = city;
                        if (selectedCity != null) {
                            //Hide keyboard to have more space
                            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            //Show city on map
                            webview.loadUrl("file:///android_asset/map.html?lat=" + selectedCity.getLatitude() + "&lon=" + selectedCity.getLongitude());
                        }
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        performDone();
                    }
                });
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private boolean insertOWMKey() {
            EditText keyInputField = findViewById(R.id.tutorial_owm_key_field);
            String currentValue = keyInputField.getText().toString().replaceAll("[\\s\\u0085\\p{Z}]", "");
            if (currentValue != null && currentValue.length() == 32) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("API_key_value", currentValue);
                editor.commit();
                return true;
            } else if (currentValue.length() > 0) {
                Toast.makeText(getApplicationContext(), R.string.insert_correct_owm_key, Toast.LENGTH_LONG).show();
                return false;
            }
            return false;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}

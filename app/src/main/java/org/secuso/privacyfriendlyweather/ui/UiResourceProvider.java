package org.secuso.privacyfriendlyweather.ui;

import org.secuso.privacyfriendlyweather.R;

/**
 * This static class provides image / icon resources for the UI.
 */
public class UiResourceProvider {

    /**
     * Private constructor in order to make this class static.
     */
    private UiResourceProvider() {
    }

    /**
     * @param categoryNumber The category number. See IApiToDatabaseConversion#WeatherCategories
     *                       for details.
     * @return Returns the icon (resource) that belongs to the given category number.
     */
    public static int getIconResourceForWeatherCategory(int categoryNumber) {
        switch (categoryNumber) {
            case 10:
                return R.mipmap.weather_icon_sunny;
            case 20:
                return R.mipmap.weather_icon_sunny_with_clouds;
            case 30:
                return R.mipmap.weather_icon_cloudy_scattered;
            case 40:
                return R.mipmap.weather_icon_clouds_broken;
            case 50:
                return R.mipmap.weather_icon_foggy;
            case 60:
                return R.mipmap.weather_icon_rain;
            case 70:
                return R.mipmap.weather_icon_rain;
            case 80:
                return R.mipmap.weather_icon_snow;
            case 90:
                return R.mipmap.weather_icon_thunderstorm;
            default:
                return R.mipmap.weather_icon_cloudy_scattered;
        }
    }

    /**
     * @param categoryNumber The category number. See IApiToDatabaseConversion#WeatherCategories
     *                       for details.
     * @return Returns the image resource that belongs to the given category number.
     */
    public static int getImageResourceForWeatherCategory(int categoryNumber) {
        switch (categoryNumber) {
            case 10:
                return R.drawable.weather_image_sunny;
            case 20:
                return R.drawable.weather_image_sunny_with_clouds;
            case 30:
                return R.drawable.weather_image_scattered_clouds;
            case 40:
                return R.drawable.weather_image_broken_clouds;
            case 50:
                return R.drawable.weather_image_foggy;
            case 60:
                return R.drawable.weather_image_rain;
            case 70:
                return R.drawable.weather_image_rain;
            case 80:
                return R.drawable.weather_image_snow;
            case 90:
                return R.drawable.weather_image_thunderstorm;
            default:
                return R.drawable.weather_image_scattered_clouds;
        }
    }

}

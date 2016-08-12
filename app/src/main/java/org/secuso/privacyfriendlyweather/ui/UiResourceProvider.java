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
        // TODO: Update when I was given the icons for the productive version
        switch (categoryNumber) {
            case 10:
                return R.drawable.weather_icon_sunny;
            case 20:
                return R.drawable.weather_icon_sunny_with_clouds;
            case 30:
                return R.drawable.weather_icon_cloudy_scattered;
            case 40:
                return R.drawable.weather_icon_cloudy_broken;
            case 50:
                return R.drawable.weather_icon_foggy;
            case 60:
                return R.drawable.weather_icon_shower_rain;
            case 70:
                return R.drawable.weather_icon_rain;
            case 80:
                return R.drawable.weather_icon_snow;
            case 90:
                return R.drawable.weather_icon_thunderstorm;
            default:
                return R.drawable.weather_icon_sunny_with_clouds;
        }
    }

    /**
     * @param categoryNumber The category number. See IApiToDatabaseConversion#WeatherCategories
     *                       for details.
     * @return Returns the image resource that belongs to the given category number.
     */
    public static int getImageResourceForWeatherCategory(int categoryNumber) {
        // TODO: Update when I was given the images for the productive version
        switch (categoryNumber) {
            case 10:
                return R.drawable.weather_image_sunny;
            case 20:
                return R.drawable.weather_image_sunny;
            case 30:
                return R.drawable.weather_image_sunny;
            case 40:
                return R.drawable.weather_image_sunny;
            case 50:
                return R.drawable.weather_image_sunny;
            case 60:
                return R.drawable.weather_image_sunny;
            case 70:
                return R.drawable.weather_image_sunny;
            case 80:
                return R.drawable.weather_image_sunny;
            case 90:
                return R.drawable.weather_image_sunny;
            default:
                return R.drawable.weather_image_sunny;
        }
    }

}

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
     * @param isDay          True if TimeStamp between sunrise and sunset
     * @return Returns the icon (resource) that belongs to the given category number.
     */
    public static int getIconResourceForWeatherCategory(int categoryNumber, boolean isDay) {
        switch (categoryNumber) {
            case 10:
                if (isDay) {
                    return R.mipmap.weather_icon_sunny;
                } else {
                    return R.mipmap.weather_icon_moon;
                }
            case 20:
                if (isDay) {
                    return R.mipmap.weather_icon_sunny_with_clouds;
                } else {
                    return R.mipmap.weather_icon_moon_with_clouds;
                }
            case 30:
                if (isDay) {
                    return R.mipmap.weather_icon_cloudy_scattered;
                } else {
                    return R.mipmap.weather_icon_moon_with_scattered_clouds;
                }
            case 40:
                return R.mipmap.weather_icon_clouds_broken;
            case 45:
                return R.mipmap.weather_icon_clouds_overcast;
            case 50:
                return R.mipmap.weather_icon_foggy;
            case 60:
                return R.mipmap.weather_icon_drizzle_rain;
            case 70:
                return R.mipmap.weather_icon_light_rain;
            case 71:
                return R.mipmap.weather_icon_moderate_rain;
            case 72:
                return R.mipmap.weather_icon_rain;
            case 80:
                return R.mipmap.weather_icon_snow;
            case 90:
                return R.mipmap.weather_icon_thunderstorm;
            default:
                if (isDay) {
                    return R.mipmap.weather_icon_cloudy_scattered;
                } else {
                    return R.mipmap.weather_icon_moon_with_scattered_clouds;
                }
        }
    }

    /**
     * @param categoryNumber The category number. See IApiToDatabaseConversion#WeatherCategories
     *                       for details.
     * @param isDay          True if TimeStamp between sunrise and sunset
     * @return Returns the image resource that belongs to the given category number.
     */
    public static int getImageResourceForWeatherCategory(int categoryNumber, boolean isDay) {
        switch (categoryNumber) {
            case 10:
                if (isDay) {
                    return R.drawable.weather_image_sunny;
                } else {
                    return R.drawable.weather_image_moon;
                }
            case 20:
                if (isDay) {
                    return R.drawable.weather_image_sunny_with_clouds;
                } else {
                    return R.drawable.weather_image_moon_with_clouds;
                }
            case 30:
                if (isDay) {
                    return R.drawable.weather_image_scattered_clouds;
                } else {
                    return R.drawable.weather_image_moon_with_scattered_clouds;
                }
            case 40:
                if (isDay) {
                    return R.drawable.weather_image_broken_clouds;
                } else {
                    return R.drawable.weather_image_night_with_broken_clouds;
                }
            case 45:
                if (isDay) {
                    return R.drawable.weather_image_overcast_clouds;
                } else {
                    return R.drawable.weather_image_night_with_overcast_clouds;
                }
            case 50:
                if (isDay) {
                    return R.drawable.weather_image_foggy;
                } else {
                    return R.drawable.weather_image_night_foggy;
                }
            case 60:
                if (isDay) {
                    return R.drawable.weather_image_drizzle_rain;
                } else {
                    return R.drawable.weather_image_night_drizzle_rain;
                }
            case 70:
                if (isDay) {
                    return R.drawable.weather_image_light_rain;
                } else {
                    return R.drawable.weather_image_night_with_light_rain;
                }
            case 71:
                if (isDay) {
                    return R.drawable.weather_image_moderate_rain;
                } else {
                    return R.drawable.weather_image_night_with_moderate_rain;
                }
            case 72:
                if (isDay) {
                    return R.drawable.weather_image_rain;
                } else {
                    return R.drawable.weather_image_night_with_rain;
                }
            case 80:
                if (isDay) {
                    return R.drawable.weather_image_snow;
                } else {
                    return R.drawable.weather_image_night_snow;
                }
            case 90:
                if (isDay) {
                    return R.drawable.weather_image_thunderstorm;
                } else {
                    return R.drawable.weather_image_night_thunderstorm;
                }
            default:
                if (isDay) {
                    return R.drawable.weather_image_scattered_clouds;
                } else {
                    return R.drawable.weather_image_moon_with_scattered_clouds;
                }
        }
    }

}

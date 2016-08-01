package org.secuso.privacyfriendlyweather.ui;

/**
 * This class represents the list items that are displayed in the city overview list.
 */
public class CityOverviewListItem {

    /**
     * Member variables.
     */
    private String text;
    private int imageId;

    /**
     * Constructor.
     *
     * @param text    The text of the list item to display.
     * @param imageId The image ID of the resource to display.
     */
    public CityOverviewListItem(String text, int imageId) {
        this.imageId = imageId;
        this.text = text;
    }

    /**
     * @return Returns the text of the list item.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text of the liste item to display.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return Returns the resource ID of the image to display.
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * @param imageId The resource ID of the image to display.
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

}

package org.secuso.privacyfriendlyweather.ui;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A static class providing various functionalities regarding the (graphical) user interface.
 */
public class UiUtils {

    /**
     * Make this class static.
     */
    private UiUtils() {
    }

    /**
     * This method checks if the text of a given TextView is entirely visible on the screen. If it
     * is not, the spaces are replaced by a line break.
     *
     * @param container The container element of the given text view. This may be a GridLayout or
     *                  LinearLayout for instance.
     * @param textView  The TextView to check and modify in case the text is too long.
     */
    public static void makeTextViewEntirelyVisible(final ViewGroup container, final TextView textView) {
        // post is necessary because we need to wait until the layout is drawn
        container.post(new Runnable() {
            @Override
            public void run() {
                // Get the width of the parent
                int containerWidth = container.getWidth();
                // For determining the width of the given text view
                Paint paint = new Paint();
                Rect bounds = new Rect();
                paint.setTextSize(textView.getTextSize());
                paint.getTextBounds(textView.getText().toString(), 0, textView.getText().length(), bounds);
                // The position of the very last / rightest pixel
                int tvLastX = (int) textView.getX() + bounds.width();
                // Too wide => insert line breaks
                if (tvLastX > containerWidth) {
                    // Not entirely visible
                    textView.setText(textView.getText().toString().replace(" ", "\n"));
                }
            }
        });
    }

}

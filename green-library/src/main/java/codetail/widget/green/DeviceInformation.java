package codetail.widget.green;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class DeviceInformation extends DualPaneLayout {

    public DeviceInformation(Context context, Bundle options) {
        super(context, options);
        setRatio(2, 3);
    }

    public void setValue(CharSequence field, CharSequence value){
        setValue(getIndexesCount(), field, value);
    }

    /**
     * Gets TextViews on {@code index}, if there is no views
     * on index inflates new one, and binds passed data
     *
     * Note, newly inflated views are preventing {@link #requestLayout()}
     * so after batch setting you need to invoke it
     *
     * @param index index of element to rebind
     * @param field fieldName on the left pane
     * @param value value on the right pane
     */
    public void setValue(int index, CharSequence field, CharSequence value){
        inflateIfNeeded(index, R.layout.config_textview, R.layout.config_textview);

        TextView fieldView = (TextView) getChildAt(K * index);
        TextView valueView = (TextView) getChildAt(K * index + 1);

        fieldView.setText(field);
        valueView.setText(value);
    }

    public void setHeading(CharSequence field){
        setHeading(getIndexesCount(), field);
    }

    /**
     * First inflated textview will be inflated and marked as heading,
     * it means it will fill full horizontal space
     *
     * @param index index of element to rebind
     * @param field fieldName on the left pane
     */
    public void setHeading(int index, CharSequence field){
        inflateIfNeeded(index, R.layout.config_textview, R.layout.config_textview);

        TextView fieldView = (TextView) getChildAt(K * index);
        fieldView.setText(field);
        LayoutParams params = (LayoutParams) fieldView.getLayoutParams();
        params.heading = true;

        TextView valueView = (TextView) getChildAt(K * index + 1);
        valueView.setText(null);
    }

    /**
     * Bind all information, after batch adding
     * {@link #requestLayout()} & {@link #invalidate()}
     * will be called
     *
     * @see #setValue(int, CharSequence, CharSequence)
     */
    protected void onBindInformation(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        setValue("Make", Build.MANUFACTURER);
        setValue("Model", Build.MODEL);
        setValue("Device", Build.DEVICE);
        setValue("Display", Build.DISPLAY);
        setValue("Resolution", dm.heightPixels + "x" + dm.widthPixels);
        setValue("Density", dm.densityDpi + "dpi (" + getDensityString(dm) + ")");
        setValue("Release",  Build.VERSION.RELEASE);
        setValue("API",  String.valueOf(Build.VERSION.SDK_INT));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onBindInformation();
        requestLayout();
        invalidate();
    }

    protected  static String getDensityString(DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return String.valueOf(displayMetrics.densityDpi);
        }
    }
}

package codetail.green.sample.cards;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;

import codetail.widget.green.ConfigCard;
import codetail.widget.green.DualPaneLayout;
import codetail.widget.green.NetworkInformation;

public class PicassoCard extends ConfigCard {

    public PicassoCard(Context context) {
        this(context, null);
    }

    public PicassoCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PicassoCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        addViewInLayout(new PicassoInformation(context, Bundle.EMPTY), getChildCount(),
                generateDefaultLayoutParams());
    }

    @Override
    protected boolean onConfigChanged(Bundle configs, String key) {
        /**
         * Every config change should be handled locally
         */
        boolean enabled = configs.getBoolean(key, false);
        if("indicators_enabled".equals(key)){
            Picasso.with(getContext())
                    .setIndicatorsEnabled(enabled);
        }else if ("logging_enabled".equals(key)) {
            Picasso.with(getContext())
                    .setLoggingEnabled(enabled);
        }
        return true;
    }

    @Override
    protected View createView(String name, Bundle defConfigs, Bundle options) {
        if("CustomCheckBox".equals(name)){
            return createCheckBox(this, defConfigs, options);
        }

        return super.createView(name, defConfigs, options);
    }

    @Override
    protected boolean handleOptions(Bundle options, String key, JsonReader reader) {
        return false;
    }

    private static class PicassoInformation extends NetworkInformation{

        public PicassoInformation(Context context, Bundle options) {
            super(context, options);
        }

        @Override
        protected void onBindInformation() {
            StatsSnapshot info = Picasso.with(getContext())
                    .getSnapshot();

            setSubHeading("Cache", String.format("%s / %s (%f percent)",
                    humanReadableByteCount(info.size, true),
                    humanReadableByteCount(info.maxSize, true),
                    ((info.size * 100f) / info.maxSize)));

            setValue("\tHits", String.valueOf(info.cacheHits));
            setValue("\tMisses", String.valueOf(info.cacheMisses));

            setSubHeading("Decoded", String.valueOf(info.downloadCount));
            setValue("\tTotal", humanReadableByteCount(info.totalDownloadSize, true));
            setValue("\tAverage", humanReadableByteCount(info.averageDownloadSize, true));

            setSubHeading("Transformed", String.valueOf(info.transformedBitmapCount));
            setValue("\tTotal", humanReadableByteCount(info.totalTransformedBitmapSize, true));
            setValue("\tAverage", humanReadableByteCount(info.averageTransformedBitmapSize, true));
        }
    }

    private static View createCheckBox(final ConfigCard parent, final Bundle defConfigs, final Bundle options){
        final String key = options.getString("key");
        final boolean defValue;

        if(defConfigs.containsKey(key)){
            defValue = defConfigs.getBoolean(key);
        }else{
            defValue = options.getBoolean("value");
            defConfigs.putBoolean(key, defValue);
        }

        DualPaneLayout layout = findDualPanelLayout(parent);
        final boolean hasLayout = layout != null;
        if(!hasLayout) {
            layout = new DualPaneLayout(parent.getContext(), options);
            layout.setRatio(3, 2);
        }

        final int index = layout.getIndexesCount();
        layout.inflateIfNeeded(index, codetail.widget.green.R.layout.config_textview, codetail.widget.green.R.layout.config_checkbox);

        final TextView title = (TextView) layout.getChildAt(DualPaneLayout.K * index);
        title.setText(options.getString("title"));

        final CheckBox radioButton = (CheckBox) layout.getChildAt(DualPaneLayout.K * index + 1);
        radioButton.setChecked(defValue);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defConfigs.putBoolean(key, isChecked);
                parent.notifyConfigChanged(key);
            }
        });

        return hasLayout ? null : layout;
    }

    private static DualPaneLayout findDualPanelLayout(ConfigCard card){
        int childCount = card.getChildCount();
        for(int index = 0; index < childCount; index++){
            View child = card.getChildAt(index);
            if((child instanceof DualPaneLayout) && !(child instanceof PicassoInformation)){
                return (DualPaneLayout) child;
            }
        }
        return null;
    }

    /**
     * Transforms given bytes to human readable string
     * 1024 -> 1KB
     *
     * @param bytes Bytes to transform
     * @param si 1024 or 1000 mode
     *
     * @return human readable bytes lengths
     */
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}

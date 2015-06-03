package codetail.widget.green;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationWidget extends LinearLayout{
    private static final int[] ATTRS = new int[]{R.attr.configCardBackground};

    private JsonMenuInflater mInflater;
    private Bundle mConfigurations;
    private boolean mRestoreState;
    private String mConfigurationJson;
    private List<OnConfigurationChange> mConfigChangeListeners;

    public ConfigurationWidget(Context context) {
        this(context, null);
    }

    public ConfigurationWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfigurationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        mInflater = new JsonMenuInflater(this);
        mConfigChangeListeners = new ArrayList<>();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = params.leftMargin = params.rightMargin = getResources().getDimensionPixelSize(R.dimen.configuration_widget_card_marginTop);
        return params;
    }

    /**
     * Returns configuration map
     */
    public Bundle getConfigurations(){
        if(mConfigurations == null){
            mConfigurations = new Bundle();
        }
        return mConfigurations;
    }

    public void notifyConfigChanged(ConfigCard widget, String key){
        if(mConfigurations == null || !mConfigurations.containsKey(key)){
            throw new RuntimeException("Configuration map doesn't have option with key=" + key);
        }

        if(mConfigChangeListeners != null){
            final int length = mConfigChangeListeners.size();
            for(int index = 0; index < length; index++){
                OnConfigurationChange listener = mConfigChangeListeners.get(index);
                listener.onChange(widget, key, mConfigurations);
            }
        }
    }

    public void addOnConfigurationChangeListener(OnConfigurationChange listener){
        if(listener == null) {
            throw new IllegalArgumentException("Listener shouldn't be null");
        }

        mConfigChangeListeners.add(listener);
    }

    public void removeOnConfigurationChangeListener(OnConfigurationChange listener){
        mConfigChangeListeners.remove(listener);
    }

    public ConfigCard getConfigCard(int index){
        CardView view = (CardView) super.getChildAt(index);
        return (ConfigCard) view.getChildAt(0);
    }

    /**
     * Adds view in the layout without sequent calling
     * of {@link #requestLayout()}, it useful for batch
     * views adding
     *
     * @param widget The widget needs to add
     */
    void addWidget(ConfigCard widget){
        CardView view = new CardView(getContext());
        view.addView(widget);
        view.setBackgroundColor(getCardBackgroundColor(getContext().getTheme()));
        view.setCardElevation(getResources().getDimensionPixelSize(R.dimen.configuration_widget_card_elevation));
        addViewInLayout(view, getChildCount(), generateDefaultLayoutParams(), false);
    }

    static int getCardBackgroundColor(Resources.Theme theme){
        TypedArray array = theme.obtainStyledAttributes(ATTRS);
        int color = array.getColor(0, 0xFFFAFAFA);
        array.recycle();
        return color;
    }

    public final void setConfigurations(String json, boolean restoreState){
        if(TextUtils.isEmpty(json)){
            return;
        }

        mRestoreState = restoreState;
        mConfigurationJson = json;

        if(!restoreState) {
            render(json);
        }
    }

    public final void setConfigurations(@RawRes int id, boolean restoreState){
        setConfigurations(getRaw(id), restoreState);
    }

    /**
     * Process given Json content, and create developer menu
     *
     * @param content the json content of developer settings
     */
    protected void render(final String content){
        removeAllViews();

        mInflater.inflate(content);
    }

    /**
     * Listen for changes in configuration menu
     */
    public interface OnConfigurationChange{

        /**
         * Called when configuration value changed
         *
         * @param key Changed configuration item key
         * @param newConfig Configurations map
         */
        void onChange(ConfigCard widget, String key, Bundle newConfig);
    }

    /**
     * Ugly but simple
     */
    String getRaw(@RawRes int id){
        InputStream stream = getResources().openRawResource(id);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder builder = new StringBuilder();
        try {
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        ConfigState state = new ConfigState(super.onSaveInstanceState());
        state.mConfigs = getConfigurations();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        ConfigState s = (ConfigState) state;
        mConfigurations = s.mConfigs;
        super.onRestoreInstanceState(s.getSuperState());

        if(mRestoreState){
            render(mConfigurationJson);
        }
    }

    static class ConfigState extends BaseSavedState{

        Bundle mConfigs;

        ConfigState(Parcel source) {
            super(source);
            mConfigs = source.readBundle();
        }

        private ConfigState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(mConfigs);
        }

        public static final Creator<ConfigState> CREATOR = new Creator<ConfigState>() {
            @Override
            public ConfigState createFromParcel(Parcel in) {
                return new ConfigState(in);
            }

            @Override
            public ConfigState[] newArray(int size) {
                return new ConfigState[size];
            }
        };
    }
}

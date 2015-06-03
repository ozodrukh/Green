package codetail.widget.green;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class ConfigCard extends LinearLayout{
    private final static String TAG = "ConfigCard";

    private TextView mTitle;

    public ConfigCard(Context context) {
        this(context, null);
    }

    public ConfigCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfigCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final int padding = getResources().getDimensionPixelSize(R.dimen.configuration_widget_padding);
        setOrientation(VERTICAL);
        setPadding(padding, padding, padding, padding);

        inflate(context, R.layout.developer_base_card, this);

        mTitle = (TextView) findViewById(R.id.DeveloperCardTitle);
    }

    public void setTitle(CharSequence title){
        mTitle.setText(title);
    }

    public CharSequence getTitle() {
        return mTitle.getText();
    }

    /**
     * Returns {@link ConfigurationWidget} where this view is
     * placed and global configuration bundle saved
     */
    public ConfigurationWidget getConfigurationWidget(){
        return (ConfigurationWidget) getParent().getParent();
    }

    /**
     * Notify that configuration with associated key was
     * changed
     *
     * Firstly it will try to manage key change by self,
     * if nothing handled will let all listeners to handle them
     *
     * @param key changed value key
     */
    public final void notifyConfigChanged(String key){
        if(!onConfigChanged(getConfigurationWidget().getConfigurations(), key)){
            getConfigurationWidget().notifyConfigChanged(this, key);
        }
    }

    /**
     * Override this method and make it as the Controller,
     * so handle any changes in this configuration card
     *
     * @param configs Global configurations map
     * @param key Key of configuration
     *
     * @return true if configuration change was handled, otherwise
     *      system will notify listeners
     */
    protected boolean onConfigChanged(Bundle configs, String key){
        return false;
    }

    /**
     * Adds a view during layout. This is useful if in your onLayout() method,
     * you need to add more views (as does the list view for example).
     *
     * If index is negative, it means put it at the end of the list.
     *
     * @param child the view to add to the group
     * @return true if the child was added, false otherwise
     */
    protected boolean appendViewInLayout(View child){
        if(child == null){
            return false;
        }

        return addViewInLayout(child, getChildCount(), generateDefaultLayoutParams(), true);
    }

    /**
     * Hook you can supply that is called when inflating from a LayoutInflater.
     * You can use this to customize the tag names available in your XML
     * layout files.
     *
     * <p>
     * Note that it is good practice to prefix these custom names with your
     * package (i.e., com.coolcompany.apps) to avoid conflicts with system
     * names.
     *
     * @param name Tag name to be inflated.
     * @param options Inflation attributes as specified in JSON.
     * @param defConfigs Global configurations, if you creating configuration
     *                   view, you should also put key with default value
     *
     * @return View Newly created view. Return null for the default
     *         behavior.
     */
    protected View createView(String name, Bundle defConfigs, Bundle options){
        return null;
    }

    /**
     * Handle parameter and put value as you desire
     * into options
     *
     * @param options Options map
     * @param key current parameter name
     *
     * @return true if {@code key} was handled, and we should
     *  advance to the next object
     */
    protected boolean handleOptions(Bundle options, String key, JsonReader reader){
        return false;
    }

    /***
     * This method called in the background, here
     * need to parse given {@code json}
     *
     * @param reader Widget configuration
     */
    protected void inflate(ConfigurationWidget widget, JsonReader reader) throws IOException {
        Bundle options = new Bundle();
        Bundle configs = widget.getConfigurations();
        JsonToken token = reader.peek();
        if(token == JsonToken.BEGIN_OBJECT){
            appendViewInLayout(createView(this, configs, options, reader));
        }else if (token == JsonToken.BEGIN_ARRAY){
            reader.beginArray();
            while(reader.peek() != JsonToken.END_ARRAY){
                appendViewInLayout(createView(this, configs, options, reader));
                options.clear(); // recycle configurations
            }
            reader.endArray();
        }else{
            throw new RuntimeException("Unsupported token exception: " + reader.toString());
        }
        options.clear();
    }

    /**
     * Creates and setups widget("type")
     *
     * @param parent Parent view where view will be added
     *
     * @param defConfigs Bundle with global configurations from
     *              {@link ConfigurationWidget#getConfigurations()}
     *
     * @param options Predefined options to create view, actually this
     *                made to don't over allocate memory, use one instance
     *                of options bundle across whole config card creating
     *
     * @param jsonOptions Started in the {@link JsonToken#BEGIN_OBJECT} where
     *                    options are described
     *
     * @return Created view based on given information
     */
    protected static View createView(ConfigCard parent, Bundle defConfigs, Bundle options, JsonReader jsonOptions){
        options = asMap(parent, options, jsonOptions);
        String viewType = options.getString("type");

        if(viewType == null){
            throw new RuntimeException(">>> View type is not provided");
        }else if(BuildConfig.DEBUG){
            Log.d(TAG, String.format(">>> Creating %s", viewType));
        }

        switch (viewType){
            case "Spinner":
                return createSpinner(parent, defConfigs, options);
            case "EditText":
                return createEditText(parent, defConfigs, options);
            case "CheckBox":
                return createCheckBox(parent, defConfigs, options);
            case "DeviceInformationModule":
                return new DeviceInformation(parent.getContext(), options);
            case "ApplicationInformationModule":
                return new ApplicationInformation(parent.getContext(), options);
            case "BuildConfigInformation":
                return new BuildConfigInformation(parent.getContext(), options);
            default:
                return parent.createView(viewType, defConfigs, options);
        }
    }

    public static Bundle asMap(ConfigCard widget, Bundle options, JsonReader reader){
        try {
            reader.beginObject();
            while(reader.peek() != JsonToken.END_OBJECT){
                final String key = reader.nextName();
                if(widget.handleOptions(options, key, reader)){
                    continue;
                }

                final JsonToken token = reader.peek();
                if(token == JsonToken.STRING){
                    options.putString(key, reader.nextString());
                }else if(token == JsonToken.BOOLEAN){
                    options.putBoolean(key, reader.nextBoolean());
                }else if(token == JsonToken.NUMBER){
                    options.putInt(key, reader.nextInt());
                }else if(!handleSystemOptions(options, key, reader)){
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return options;
    }

    private static boolean handleSystemOptions(Bundle options, String key, JsonReader reader){
        try {
            if (key.equals("entities")) {
                reader.beginObject();
                ArrayList<String> entities = new ArrayList<>();
                while(reader.peek() != JsonToken.END_OBJECT){
                    reader.skipValue(); //skip name
                    entities.add(reader.nextString());
                }
                reader.endObject();
                options.putStringArrayList(key, entities);
                return true;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private static View createEditText(final ConfigCard parent, final Bundle defConfigs, final Bundle options){
        final Context context = parent.getContext();

        // Register config key & default value
        final String key = options.getString("key");
        final String value = Utils.getString("value", defConfigs, options);
        defConfigs.putString(key, value);

        EditText editText = (EditText) LayoutInflater.from(context)
                .inflate(R.layout.config_edittext, parent, false);
        editText.setHint(options.getString("hint"));
        editText.setText(value);
        editText.setFreezesText(true);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    defConfigs.putString(key, v.getText().toString());
                    parent.notifyConfigChanged(key);
                    return true;
                }
                return false;
            }
        });
        return editText;
    }

    private static View createSpinner(final ConfigCard parent, final Bundle defConfigs, final Bundle options){
        final Context context = parent.getContext();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                options.getStringArrayList("entities"));

        // Register config key & default value
        final String key = options.getString("key");
        final int selected = defConfigs.getInt(key, options.getInt("value", 0));
        defConfigs.putString(key, adapter.getItem(selected));

        Spinner spinner = (Spinner) LayoutInflater.from(context)
                .inflate(R.layout.config_spinner, parent, false);
        spinner.setAdapter(adapter);
        spinner.setSelection(selected);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View view, int position, long id) {
                defConfigs.putInt(key, position);
                parent.notifyConfigChanged(key);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return spinner;
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
        layout.inflateIfNeeded(index, R.layout.config_textview, R.layout.config_checkbox);

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

    static DualPaneLayout findDualPanelLayout(ConfigCard card){
        int childCount = card.getChildCount();
        for(int index = 0; index < childCount; index++){
            View child = card.getChildAt(index);
            if(child instanceof DualPaneLayout){
                return (DualPaneLayout) child;
            }
        }
        return null;
    }
}
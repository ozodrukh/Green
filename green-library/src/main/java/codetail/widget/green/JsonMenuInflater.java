package codetail.widget.green;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.LayoutInflater;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class JsonMenuInflater{

    private static final Class<?>[] sConstructorSignature = new Class[] {Context.class};
    private static final HashMap<String, Constructor<? extends ConfigCard>> sConstructorMap = new HashMap<>();

    private ConfigurationWidget mConfigurationWidget;
    private LayoutInflater mFactory;

    public JsonMenuInflater(ConfigurationWidget menu){
        mConfigurationWidget = menu;
    }

    /**
     * Process json resource and builds {@link ConfigurationWidget}
     * based on it, process running on the background thread,
     * it means no UI glitches should be provided
     *
     * @param jsonContent Configuration file
     */
    public void inflate(String jsonContent){
        internalInflate(jsonContent);

        mConfigurationWidget.requestLayout();
        mConfigurationWidget.invalidate();
    }

    /**
     * Returns {@link ConfigurationWidget} that needs to inflate,
     */
    public ConfigurationWidget getDeveloperMenu(){
        return mConfigurationWidget;
    }

    public Context getContext(){
        return mConfigurationWidget.getContext();
    }

    /**
     * Returns main inflater of views
     */
    public LayoutInflater getLayoutInflater() {
        if(mFactory == null){
            mFactory = LayoutInflater.from(getContext());
        }
        return mFactory;
    }

    ConfigurationWidget internalInflate(String content){
        JsonReader jReader = new JsonReader(new StringReader(content));
        try {
            jReader.beginObject();
            while(jReader.peek() != JsonToken.END_OBJECT){
                String title = jReader.nextName();
                String className = null;
                if(jReader.peek() == JsonToken.STRING){
                    className = jReader.nextString();
                }
                mConfigurationWidget.addWidget(create(mConfigurationWidget, className, title, jReader));
            }
            jReader.endObject();
        } catch (IOException e) {
            throw new IllegalStateException("JSON Configuration root should be object");
        }finally {
            try {
                jReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mConfigurationWidget;
    }

    /**
     * Create new {@link ConfigCard} object, than inflates
     * that widget from JSON resource
     *
     * @param title Title that should appear on {@link ConfigCard}
     * @param reader The reader started on a new object
     * @return Config   ured & created {@link ConfigCard} element
     *
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected ConfigCard create(ConfigurationWidget parent, String className,
                                String title, JsonReader reader) throws IOException {
        ConfigCard widget = null;
        if(TextUtils.isEmpty(className)){
            widget = new ConfigCard(getContext());;
        }else{
            try {
                Constructor<? extends ConfigCard> constructor = sConstructorMap.get(className);
                if(constructor == null){
                    constructor = (Constructor<? extends ConfigCard>)
                            Class.forName(className)
                                    .getConstructor(sConstructorSignature);
                    constructor.setAccessible(true);
                    sConstructorMap.put(className, constructor);
                }
                widget = constructor.newInstance(parent.getContext());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        if(widget == null){
            throw new IllegalArgumentException("Unable to create custom ConfigCard >>>" + className);
        }

        widget.setTitle(title);
        widget.inflate(parent, reader);
        return widget;
    }

    static void skipRemainedValues(JsonReader reader, JsonToken endToken) throws IOException{
        while (reader.peek() != endToken){
            reader.skipValue();
        }

        if(endToken == JsonToken.END_ARRAY){
            reader.endArray();
        }else {
            reader.endObject();
        }

    }
}

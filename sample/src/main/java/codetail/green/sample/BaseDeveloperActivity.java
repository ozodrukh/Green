package codetail.green.sample;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import codetail.widget.green.ConfigCard;
import codetail.widget.green.ConfigurationWidget;

public class BaseDeveloperActivity extends AppCompatActivity
        implements ConfigurationWidget.OnConfigurationChange{

    private ConfigurationWidget mConfigurationWidget;
    private boolean mFreshCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFreshCreate = savedInstanceState == null;
    }

    protected boolean isFreshCreate(){
        return mFreshCreate;
    }

    /**
     * Attaches {@link codetail.widget.green.ConfigurationWidget} if current
     * build type is {@link BuildConfig#DEBUG}
     *
     * @param layout The base layout where to add {@link codetail.widget.green.ConfigurationWidget}
     */
    public void attachConfigurationWidgetIfDebugBuild(DrawerLayout layout){
        if(BuildConfig.DEBUG){
            getLayoutInflater().inflate(R.layout.external_config_widget, layout, true);
            onConfigurationWidgetAttached(mConfigurationWidget = (ConfigurationWidget)
                    findViewById(R.id.ConfigWidget));
        }
    }

    /**
     * After invoking {@link #attachConfigurationWidgetIfDebugBuild(DrawerLayout)}
     * if {@link ConfigurationWidget} was inflated this method will be called
     *
     * Setup configuration widget as you want
     *
     * @param widget inflated ConfigurationWidget instance
     */
    public void onConfigurationWidgetAttached(ConfigurationWidget widget){
        //setup
    }

    @Override
    public void onChanged(ConfigCard widget, String key, Bundle newConfig) {

    }
}

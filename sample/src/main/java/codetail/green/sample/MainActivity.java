package codetail.green.sample;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import codetail.widget.green.ConfigCard;
import codetail.widget.green.ConfigurationWidget;

public class MainActivity extends BaseDeveloperActivity {

    private DrawerLayout mRootContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootContainer = (DrawerLayout) findViewById(R.id.RootContainer);
        attachConfigurationWidgetIfDebugBuild(mRootContainer);
    }

    @Override
    public void onConfigurationWidgetAttached(ConfigurationWidget widget) {
        widget.setConfigurations(R.raw.config_file_sample_base, !isFreshCreate());
    }

    @Override
    public void onChanged(ConfigCard widget, String key, Bundle newConfig) {

    }
}

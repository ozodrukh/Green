package codetail.green.sample;

import android.os.Bundle;

import codetail.widget.green.ConfigurationWidget;

public class DeveloperActivity extends MainActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConfigurationWidget widget = (ConfigurationWidget) findViewById(R.id.ConfigWidget);
        widget.setConfigurations(R.raw.config_file_sample_base, savedInstanceState != null);
    }

}

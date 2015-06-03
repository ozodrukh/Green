package codetail.green.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import codetail.widget.green.ConfigurationWidget;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConfigurationWidget widget = (ConfigurationWidget) findViewById(R.id.ConfigWidget);
        if(savedInstanceState == null) {
            widget.setConfigurations(R.raw.config_file_sample_base);
        }
    }
}

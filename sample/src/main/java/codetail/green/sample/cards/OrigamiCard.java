package codetail.green.sample.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.util.JsonReader;

import java.io.IOException;

import codetail.widget.green.ConfigCard;
import codetail.widget.green.ConfigurationWidget;

public class OrigamiCard extends ConfigCard{

    public OrigamiCard(Context context) {
        this(context, null);
    }

    public OrigamiCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrigamiCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        SpringConfiguratorView conf = new SpringConfiguratorView(context);
        addView(conf);

        conf.refreshSpringConfigurations();
    }

    @Override
    protected void inflate(ConfigurationWidget widget, JsonReader reader) throws IOException {
        reader.skipValue();
    }
}

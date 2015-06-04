package codetail.green.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import codetail.widget.green.ConfigCard;
import codetail.widget.green.ConfigurationWidget;

public class TakeAScreenCard extends ConfigCard {

    public TakeAScreenCard(Context context) {
        this(context, null);
    }

    public TakeAScreenCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TakeAScreenCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Button button = new Button(context);
        button.setText("Take a screen");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View widget = getConfigurationWidget();
                Bitmap holder = Bitmap.createBitmap(widget.getWidth(), widget.getHeight(),
                        Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(holder);
                widget.draw(canvas);

                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                try {
                    FileOutputStream stream = new FileOutputStream(new File(path,
                            String.format("ConfigurationWidget(%s).jpg", System.currentTimeMillis())));
                    holder.compress(Bitmap.CompressFormat.PNG, 100, stream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        addView(button);
    }

    @Override
    protected void inflate(ConfigurationWidget widget, JsonReader reader) throws IOException {
        /**
         * We doesn't need any extra views or even options,
         * it will be just Screenshot button (may be later something more),
         * so we skip json object
         *
         * without skiping value further parsing will be crashed with application
         */
        reader.skipValue();
    }
}

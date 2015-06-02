package codetail.widget.green;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.assertj.android.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class JsonMenuInflaterTest {

    @Rule
    public ActivityTestRule<Activity> mActivityRule =
            new ActivityTestRule<>(Activity.class);

    @Test
    public void testJson2BundleConverter(){
        final Activity context = mActivityRule.getActivity();
        final ConfigCard widget = new ConfigCard(context);
        Bundle options = null;
        try {
            final JsonReader reader = new JsonReader(new
                    StringReader(getRaw("config_file_sample_base.json")));

            //checking only first json object
            reader.beginObject();
            reader.skipValue();
            options = ConfigCard.asMap(widget, new Bundle(), reader);

            JsonMenuInflater.skipRemainedValues(reader, JsonToken.END_OBJECT);
            reader.close();
        }catch (IOException exception){
            exception.printStackTrace();
        }

        assertNotNull(options);

        assertThat(options)
                .isNotEmpty()
                .hasSize(3)
                .hasKey("key")
                .hasKey("type")
                .hasKey("entities");

        assertEquals("productFlavor", options.getString("key"));
        assertEquals("spinner", options.getString("type"));

        ArrayList<String> expected = new ArrayList<>();
        expected.add("ProductionMode");
        expected.add("DeveloperMode");

        assertEquals(expected, options.getStringArrayList("entities"));
    }

    @Test
    public void testMenuInflater() throws IOException{
        final Activity context = mActivityRule.getActivity();
        final ConfigurationWidget configurationWidget = new ConfigurationWidget(context);;
        configurationWidget.post(new Runnable() {
            @Override
            public void run() {

                try {
                    new JsonMenuInflater(configurationWidget)
                            .inflate(getRaw("config_file_sample_base.json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assertThat(configurationWidget).hasChildCount(2);

                for(int index = 0; index < configurationWidget.getChildCount(); index++){
                    assertThat(configurationWidget.getChildAt(index))
                            .isInstanceOf(ConfigCard.class);

                    ConfigCard child = (ConfigCard) configurationWidget.getChildAt(index);

                    assertThat(((LinearLayout.LayoutParams) child.getLayoutParams()))
                            .hasTopMargin(context.getResources().getDimensionPixelSize(R.dimen.configuration_widget_card_marginTop));

                    if(index == 0) {
                        assertEquals("Handle Application Mode", getTitle(child));
                        assertThat(child.getChildAt(1))
                                .isInstanceOf(Spinner.class);
                    }else {
                        assertEquals("Device info", getTitle(child));
                    }
                }
            }
        });
    }

    @Test
    public void testJsonReader() throws IOException{
        final Activity context = mActivityRule.getActivity();
        final JsonReader reader = new JsonReader(new StringReader(
                getRaw("config_file_sample_base.json")));

        reader.beginObject();
        JsonMenuInflater.skipRemainedValues(reader, JsonToken.END_OBJECT);
        assertEquals(true, reader.peek() == JsonToken.END_DOCUMENT);
    }

    /**
     * Ugly but simple
     */
    static String getRaw(Context context, @RawRes int id){
        InputStream stream = context.getResources().openRawResource(id);
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

    static CharSequence getTitle(View view){
        return ((ConfigCard) view).getTitle();
    }

    static String getRaw(String resName) throws IOException {
        return IOUtils.toString(JsonMenuInflaterTest.class.getClassLoader()
                .getResourceAsStream("res/raw/"+resName));
    }
}

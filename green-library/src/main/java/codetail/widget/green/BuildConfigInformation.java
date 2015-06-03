package codetail.widget.green;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Primary action of file is to retrieve BuildConfig static
 * fields and show it on the desk
 */
@SuppressLint("ViewConstructor")
public class BuildConfigInformation extends ApplicationInformation{

    private final String mConfigFile;

    public BuildConfigInformation(Context context, Bundle options) {
        super(context, options);
        /***
         * You can specify custom object static fields
         * to retrieve and show on the desk
         */
        mConfigFile = Utils.getString(options.getString("object"), getBuildConfigPath(context));
    }

    @Override
    protected void onBindInformation() {
        try {
            Class clazz = Class.forName(mConfigFile);
            final Field[] fields = clazz.getFields();
            final int length = fields.length;
            for(int index = 0; index < length; index++){
                Field field = fields[index];
                if(Modifier.isStatic(field.getModifiers())) {
                    setValue(convertNameToReadableName(field.getName()), String.valueOf(field.get(null)));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static String getBuildConfigPath(Context context){
        return context.getPackageName() + ".BuildConfig";
    }

    static String convertNameToReadableName(String fieldName){
        return fieldName.replace("_", " ").toLowerCase();
    }
}

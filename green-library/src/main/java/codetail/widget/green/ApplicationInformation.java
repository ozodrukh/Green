package codetail.widget.green;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

@SuppressLint("ViewConstructor")
public class ApplicationInformation extends DeviceInformation {

    public ApplicationInformation(Context context, Bundle options) {
        super(context, options);
    }

    @Override
    protected void onBindInformation() {
        final Context context = getContext();

        ApplicationInfo ai = context.getApplicationInfo();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(ai.packageName, 0);
            setValue("Application", getContext().getString(ai.labelRes));
            setValue("Package", ai.packageName);
            setValue("Version Name", pi.versionName);
            setValue("Version Code", String.valueOf(pi.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

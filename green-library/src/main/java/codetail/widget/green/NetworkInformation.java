package codetail.widget.green;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.net.TrafficStats.getTotalRxBytes;
import static android.net.TrafficStats.getTotalRxPackets;
import static android.net.TrafficStats.getTotalTxBytes;
import static android.net.TrafficStats.getTotalTxPackets;
import static android.net.TrafficStats.getUidRxBytes;
import static android.net.TrafficStats.getUidRxPackets;
import static android.net.TrafficStats.getUidTxBytes;
import static android.net.TrafficStats.getUidTxPackets;
import static codetail.widget.green.Utils.getString;
import static codetail.widget.green.Utils.humanReadableByteCount;

@SuppressLint("ViewConstructor")
public class NetworkInformation extends ApplicationInformation{
    public static final int SHOW_ONLY_ACTIVE_NETOWRK_INFO = "only_active_network".hashCode();
    public static final int SHOW_ONLY_TRAFFIC_STATS = "only_traffic_stats".hashCode();
    public static final int SHOW_ALL = "all".hashCode();

    private static final Object HEADING = new RelativeSizeSpan(1.1f);
    private static final Object SUBHEADING = new RelativeSizeSpan(1.05f);
    private static final Object BOLD = new StyleSpan(Typeface.BOLD);
    private static final long DEFAULT_UPDATE_PERIOD = TimeUnit.MINUTES.toMillis(10);

    private int mFlag;

    private boolean mUpdateAllowed;
    private long mUpdateDelay;

    private ConnectivityManager mConnectivityManager;
    private Runnable mUpdateStats = new Runnable() {
        @Override
        public void run() {
            rebindInformation();
            if(mUpdateAllowed) {
                postDelayed(this, mUpdateDelay);
            }
        }
    };

    public NetworkInformation(Context context, Bundle options) {
        super(context, options);
        mFlag = getString(options.getString("show_flag"), "all").hashCode();
        if(mFlag != SHOW_ONLY_ACTIVE_NETOWRK_INFO && mFlag != SHOW_ONLY_TRAFFIC_STATS
                && mFlag != SHOW_ALL){
            throw new IllegalArgumentException("showFlag should be one of following values 'all', " +
                    "'only_traffic_stats', 'only_active_network'");
        }

        // By default we doesn't support long values in json
        mUpdateDelay = options.getInt("update_delay");
        if(mUpdateDelay == 0){
            mUpdateDelay = DEFAULT_UPDATE_PERIOD;
        }

        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setUpdateDelay(long ms){
        mUpdateDelay = ms;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUpdateAllowed = true;
        postDelayed(mUpdateStats, mUpdateDelay);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUpdateAllowed = false;
        removeCallbacks(mUpdateStats);
    }

    @Override
    protected void onBindInformation() {
        if(isNetworkStatsAvaialable() && mFlag != SHOW_ONLY_TRAFFIC_STATS){
            createActiveNetworkInformation();
        }

        if(mFlag != SHOW_ONLY_ACTIVE_NETOWRK_INFO) {
            createTrafficStats();
        }
    }

    protected void rebindInformation(){
        removeAllViewsInLayout();
        onBindInformation();

        invalidate();
        requestLayout();
    }

    protected void createTrafficStats(){
        final int uid = android.os.Process.myUid();
        setSubHeading("Application Bytes: ");

        setTrafficValue("\tsent", getUidTxBytes(uid));
        setTrafficValue("\treceived", getUidRxBytes(uid));

        setSubHeading("Application Packets: ");
        setTrafficValue("\tsent", getUidTxPackets(uid));
        setTrafficValue("\treceived", getUidRxPackets(uid));

        setSubHeading("Total Bytes: ");

        setTrafficValue("\tsent", getTotalTxBytes());
        setTrafficValue("\treceived", getTotalRxBytes());

        setSubHeading("Total Packets: ");
        setTrafficValue("\tsent", getTotalTxPackets());
        setTrafficValue("\treceived", getTotalRxPackets());
    }

    @Override
    public void setHeading(CharSequence string){
        SpannableString heading = new SpannableString(string);
        heading.setSpan(HEADING, 0, string.length(), 0);
        super.setHeading(heading);
    }

    public void setSubHeading(CharSequence string){
        SpannableString heading = new SpannableString(string);
        heading.setSpan(SUBHEADING, 0, string.length(), 0);
        super.setHeading(heading);
    }

    public void setSubHeading(CharSequence string, String value){
        SpannableString heading = new SpannableString(string);
        heading.setSpan(SUBHEADING, 0, string.length(), 0);
        setValue(heading, value);
    }

    public void setTrafficValue(CharSequence field, long value) {
        setValue(field, humanReadableByteCount(value, true));
    }

    /**
     * Determines whether {@link android.Manifest.permission#ACCESS_NETWORK_STATE}
     * is granted to package or not
     *
     * @return true if {@link android.Manifest.permission#ACCESS_NETWORK_STATE}
     *  is granted otherwise false
     */
    protected final boolean isNetworkStatsAvaialable(){
        final Context context = getContext();
        boolean granted = true;
        if(context.checkCallingOrSelfPermission(ACCESS_NETWORK_STATE) != PERMISSION_GRANTED){
            granted = false;
            setHeading("Network state is unavailable, add permission to the manifest");
        }
        return granted;
    }

    private void createActiveNetworkInformation(){
        NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();
        if(ni == null) {
            setHeading("No active networks");
        }else {
            setValue("Network type", ni.getTypeName() + "[" + ni.getType() + "]");
            setValue("Network subtype", ni.getSubtypeName() + "[" + ni.getSubtype() + "]");
            setValue("Connected", Boolean.toString(ni.isConnected()));
            setValue("Roaming", Boolean.toString(ni.isRoaming()));
            setValue("State", ni.getState().name());
            setValue("Detailed state", ni.getDetailedState().name());
            setValue("Reason", (ni.getReason() == null) ? "(unspecified)" : ni.getReason());
            setValue("Extra", (ni.getExtraInfo() == null) ? "(none)" : ni.getExtraInfo());
        }
    }
}

package codetail.widget.green;

import android.os.Bundle;

final class Utils {

    /**
     * Return optional value if primary is null
     */
    static String getString(String key, Bundle primary, Bundle optional){
        final String value = primary.getString(key);
        return (value == null) ? optional.getString(key) : value;
    }

    /**
     * Return optional value if primary is null
     */
    static String getString(String primary, String optional){
        return (primary == null) ? optional : primary;
    }


    /**
     * Transforms given bytes to human readable string
     * 1024 -> 1KB
     *
     * @param bytes Bytes to transform
     * @param si 1024 or 1000 mode
     *
     * @return human readable bytes lengths
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}

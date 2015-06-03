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


}

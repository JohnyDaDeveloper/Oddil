package cz.johnyapps.oddil.user;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PublicInfo {
    private static final String TAG = "PublicInfo";

    public String name;
    public String about;

    public void fromMap(@Nullable Map<String, Object> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                switch (key) {
                    case "name": {
                        name = (String) map.get(key);
                        break;
                    }

                    case "about": {
                        about = (String) map.get(key);
                        break;
                    }

                    default: {
                        Log.d(TAG, "fromMap: unknown key (" + key + ")");
                        break;
                    }
                }
            }
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("about", about);

        return map;
    }
}

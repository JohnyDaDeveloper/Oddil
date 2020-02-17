package cz.johnyapps.oddil.user;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PrivateInfo {
    private static final String TAG = "PrivateInfo";

    public String email;

    public void fromMap(@Nullable Map<String, Object> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                switch (key) {
                    case "email": {
                        email = (String) map.get(key);
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
        map.put("email", email);

        return map;
    }
}

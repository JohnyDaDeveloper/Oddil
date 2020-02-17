package cz.johnyapps.oddil;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboardUtils {
    private static final String TAG = "SoftKeyboardUtils";

    public static void hideKeyboardFrom(Context context, View... views) {
        for (View view : views) {
            if (view.isFocused()) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    Log.d(TAG, "hideKeyboardFrom: hiding...");
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    view.clearFocus();
                } else {
                    Log.e(TAG, "hideKeyboardFrom: InputMethodManager not found");
                }
            } else {
                Log.w(TAG, "hideKeyboardFrom: view not focused");
            }
        }
    }
}

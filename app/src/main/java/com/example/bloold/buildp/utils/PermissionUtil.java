package com.example.bloold.buildp.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by bloold on 25.10.17.
 */

public class PermissionUtil {
    public static boolean checkIsPermissionNeeded(Activity activity,
                                                  String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED)
                return true;
        }

        return false;
    }

    public static boolean checkIsShouldRequestPermissionsRationale(Activity activity,
                                                                   String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true;
        }

        return false;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.w("PERMISSIONS", "Permission not granted");
                return false;
            }
        }
        Log.w("PERMISSIONS", "Permissions granted");
        return true;
    }
}

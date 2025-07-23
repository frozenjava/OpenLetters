package net.frozendevelopment.openletters.extensions

import android.content.Context
import android.content.pm.PackageManager

fun Context.isPermissionGranted(permission: String): Boolean = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

val Context.appVersion: String
    get() {
        return packageManager.getPackageInfo(packageName, 0).versionName ?: "0.0.0"
    }

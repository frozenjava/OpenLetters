package net.frozendevelopment.openletters.extensions

import android.content.Context
import android.content.pm.PackageManager

fun Context.isPermissionGranted(permission: String): Boolean {
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

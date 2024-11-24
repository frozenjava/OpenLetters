package net.frozendevelopment.openletters.extensions

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController

fun <T : Any> NavController.newRoot(destination: T) {
    if (currentDestination?.route == destination::class.java.name) {
        return
    }

    this.navigate(route = destination) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

fun NavController.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

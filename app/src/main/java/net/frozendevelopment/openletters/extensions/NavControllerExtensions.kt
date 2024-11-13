package net.frozendevelopment.openletters.extensions

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
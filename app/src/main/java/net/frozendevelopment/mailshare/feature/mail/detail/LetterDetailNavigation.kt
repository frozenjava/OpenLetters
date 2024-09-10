package net.frozendevelopment.mailshare.feature.mail.detail

import android.os.Build
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.bundle.Bundle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.typeOf

@Serializable
data class LetterDetailDestination(val letterId: LetterId)

val LetterIdNavType = object : NavType<LetterId>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): LetterId? {
        return bundle.getString(key).let {
            if (it == null) null else LetterId(it)
        }
    }

    override fun parseValue(value: String): LetterId {
        return LetterId(value)
    }

    override fun serializeAsValue(value: LetterId): String {
        return value.value
    }

    override fun put(bundle: Bundle, key: String, value: LetterId) {
        bundle.putString(key, value.value)
    }
}

fun NavGraphBuilder.letterDetail(
    navController: NavController,
) {
    composable<LetterDetailDestination>(
        typeMap = mapOf(typeOf<LetterId>() to LetterIdNavType),
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<LetterDetailDestination>()
        val viewModel: LetterDetailViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(viewModel) {
            withContext(Dispatchers.IO) {
                viewModel.load(destination.letterId)
            }
        }

        LetterDetailView(
            state = state,
            onBackClicked = navController::popBackStack
        )
    }
}

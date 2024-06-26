package fr.paug.androidmakers.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.androidmakers.ui.about.AboutActions
import com.androidmakers.ui.model.Lce
import fr.androidmakers.domain.model.SpeakerId
import fr.androidmakers.domain.model.User
import fr.paug.androidmakers.ui.components.session.SessionDetailLayout
import fr.paug.androidmakers.ui.components.session.SessionDetailViewModel
import fr.paug.androidmakers.ui.components.speakers.details.SpeakerDetailsRoute
import fr.paug.androidmakers.ui.components.speakers.details.SpeakerDetailsViewModel
import fr.paug.androidmakers.ui.navigation.MainNavigationRoute
import org.koin.androidx.compose.koinViewModel

/**
 * The main layout: entry point of the application
 */
@Composable
fun MainLayout(aboutActions: AboutActions, user: User?) {
  val mainNavController = rememberNavController()
  MainNavHost(
      mainNavController = mainNavController,
      onSessionClick = { sessionId, roomId, startTimestamp, endTimestamp ->
        mainNavController.navigate("${MainNavigationRoute.SESSION_DETAIL.name}/$sessionId")
      },
      aboutActions = aboutActions,
      user = user,
      navigateToSpeakerDetails = { speakerId ->
        mainNavController.navigate("${MainNavigationRoute.SPEAKER_DETAIL.name}/$speakerId")
      },
  )
}

@Composable
private fun MainNavHost(
    mainNavController: NavHostController,
    onSessionClick: (sessionId: String, roomId: String, startTimestamp: Long, endTimestamp: Long) -> Unit,
    aboutActions: AboutActions,
    user: User?,
    navigateToSpeakerDetails: (SpeakerId) -> Unit,
) {
  NavHost(
      navController = mainNavController,
      startDestination = MainNavigationRoute.AVA.name
  ) {

    composable(route = MainNavigationRoute.AVA.name) {
      AVALayout(
          onSessionClick = onSessionClick,
          aboutActions = aboutActions,
          user = user,
          navigateToSpeakerDetails = navigateToSpeakerDetails
      )
    }

    composable(
        route = "${MainNavigationRoute.SESSION_DETAIL.name}/{sessionId}",
        arguments = listOf(
            navArgument("sessionId") {
              type = NavType.StringType
            }
        )
    ) {

      val sessionDetailViewModel: SessionDetailViewModel = koinViewModel()

      val sessionDetailState by sessionDetailViewModel.sessionDetailState.collectAsState(
          initial = Lce.Loading
      )
      SessionDetailLayout(
          sessionDetailState = sessionDetailState,
          onBackClick = { mainNavController.popBackStack() },
          onBookmarkClick = { bookmarked -> sessionDetailViewModel.bookmark(bookmarked) },
      )
    }

    composable(
        route = "${MainNavigationRoute.SPEAKER_DETAIL.name}/{speakerId}",
        arguments = listOf(
            navArgument("speakerId") {
              type = NavType.StringType
            }
        )
    ) {
      val speakerDetailsViewModel: SpeakerDetailsViewModel = koinViewModel()

      SpeakerDetailsRoute(
          speakerDetailsViewModel = speakerDetailsViewModel,
          onBackClick = { mainNavController.popBackStack() },
      )
    }

  }
}

@Preview
@Composable
private fun MainLayoutPreview() {
  MainLayout(aboutActions = AboutActions(), user = null)
}

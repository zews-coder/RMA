package rma.catquiz.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import rma.catquiz.cats.details.catDetailsScreen
import rma.catquiz.cats.gallery.catGalleryScreen
import rma.catquiz.cats.leaderboard.leaderboardScreen
import rma.catquiz.cats.list.catsListScreen
import rma.catquiz.cats.login.loginScreen
import rma.catquiz.cats.quiz.quizScreen
import rma.catquiz.cats.result.resultScreen
import rma.catquiz.user.edit.editScreen
import rma.catquiz.user.history.historyScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { slideInHorizontally { it } },
        exitTransition = {  slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it }}
    ) {

        loginScreen(
            route = "login?add-new-user={addNewUser}",
            arguments = listOf(navArgument("addNewUser"){
                defaultValue = false
                type = NavType.BoolType
            }),
            navController = navController,
        )

        catsListScreen(
            route = "cats",
            navController = navController,
            goToQuiz = {
                navController.navigate("quiz")
            },

        )

        catDetailsScreen(
            route = "cats/{id}",
            navController = navController,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        )

        catGalleryScreen(
            route = "images/{id}",
            navController = navController,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            }),
            onPhotoClicked = {id,photoIndex->
                navController.navigate(route = "photo/${id}/${photoIndex}")
            }
        )

//        catPhotoScreen(
//            route = "photo/{id}/{photoIndex}",
//            navController = navController,
//            arguments = listOf(navArgument("id") {
//                type = NavType.StringType
//            }, navArgument("photoIndex") {
//                type = NavType.IntType
//            }),
//        )

        quizScreen(
            route = "quiz",
            navController = navController
        )

        resultScreen(
            route = "quiz/result/{category}/{result}",
            navController = navController,
            arguments = listOf(
                navArgument("category") {
                type = NavType.IntType
            }, navArgument("result") {
            type = NavType.FloatType
            }  )
        )

        leaderboardScreen(
            route = "quiz/leaderboard/{category}",
            navController = navController,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.IntType
                }
            )
        )

        historyScreen(
            route = "history",
            navController = navController
        )

        editScreen(
            route = "user/edit",
            navController = navController
        )
    }
}

inline val SavedStateHandle.catId: String
    get() = checkNotNull(get("id")) {"catId is mandatory"}
inline val SavedStateHandle.category: Int
    get() = checkNotNull(get("category")) {"category is mandatory"}
inline val SavedStateHandle.result: Float
    get() = checkNotNull(get("result")) {"result is mandatory"}
inline val SavedStateHandle.addNewUser: Boolean
    get() = get("addNewUser") ?: false

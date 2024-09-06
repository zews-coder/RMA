package rma.catquiz.cats.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import rma.catquiz.ui.AppIconButton
import rma.catquiz.ui.CustomRippleTheme
import rma.catquiz.ui.ProgressBarOurs

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.quizScreen(
    route: String,
    navController: NavController
) = composable(route = route) {
    val quizViewModel: QuizViewModel = hiltViewModel()
    val quizState by quizViewModel.questionState.collectAsState()


    Surface(
        tonalElevation = 1.dp
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Quiz", style = MaterialTheme.typography.labelLarge)
                    },
                    navigationIcon = {
                        AppIconButton(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = {
                                quizViewModel.setQuestionEvent(IQuizContract.QuizUIEvent.Exit)
                            })
                    }
                )
            },
            content = {
                if (quizState.isLoading) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(it)) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Please wait for all cat images to load", style = MaterialTheme.typography.titleSmall)
                            Text(text = "This process will take a few seconds", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.padding(top = 10.dp))
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    if (quizState.showQuizExitDialog) {
                        ExitConfirmationDialog(
                            onConfirm = {
                                navController.navigateUp()
                            },
                            onDismiss = {
                                quizViewModel.setQuestionEvent(IQuizContract.QuizUIEvent.Exit)
                            }
                        )
                    }
                    else
                    if ((quizState.questionIndex == 19 || quizState.timer <= 0) && quizState.result != null) {
                        navController.navigate("quiz/result/3/${quizState.result?.result ?: 0}")
                    }
                    else {
                        QuizScreen(
                            paddingValues = it,
                            quizViewModel = quizViewModel,
                            quizState = quizState,
                            onClickImage = { uiEvent -> quizViewModel.setQuestionEvent(uiEvent) },
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun QuizScreen(
    paddingValues: PaddingValues,
    quizViewModel: QuizViewModel,
    quizState: IQuizContract.QuizState,
    onClickImage: (uiEvent: IQuizContract.QuizUIEvent) -> Unit,
) {

    val question = quizState.questions[quizState.questionIndex]
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        ProgressBarOurs(index = quizState.questionIndex, size = quizState.questions.size)
        Column(
            modifier = Modifier.padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            Card(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = question.questionText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(text = quizState.getTimeAsFormat(), style = MaterialTheme.typography.bodyLarge)
                Text(text = "${quizState.questionIndex + 1}/20", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompositionLocalProvider(
                LocalRippleTheme provides CustomRippleTheme(
                    color =
                    if (quizViewModel.isCorrectAnswer(question.cats[0].id))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            onClickImage(
                                IQuizContract.QuizUIEvent.QuestionAnswered(
                                    catAnswer = question.cats[0]
                                )
                            )
                        },
                    model = question.images[0],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
            }
            CompositionLocalProvider(
                LocalRippleTheme provides CustomRippleTheme(
                    color =
                    if (quizViewModel.isCorrectAnswer(question.cats[1].id))
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.error
                )
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            onClickImage(
                                IQuizContract.QuizUIEvent.QuestionAnswered(
                                    catAnswer = question.cats[1]
                                )
                            )
                        },
                    model = question.images[1],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Yes")
            }
        },
        title = {
            Text("Exiting the quiz?")
        },
        text = {
            Text("Are you sure you want to exit the quiz?")
        }
    )
}
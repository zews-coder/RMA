package rma.catquiz.cats.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rma.catquiz.cats.entities.CatService
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.di.DispatcherProvider
import rma.catquiz.ui.seeResults
import rma.catquiz.user.QuizResult
import rma.catquiz.user.UserDataStore
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val catService: CatService,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    private val _questionState =
        MutableStateFlow(IQuizContract.QuizState(userData = userDataStore.data.value))
    val questionState = _questionState.asStateFlow()

    private val _questionEvent = MutableSharedFlow<IQuizContract.QuizUIEvent>()

    private var timerJob: Job? = null

    private fun setQuestionState(update: IQuizContract.QuizState.() -> IQuizContract.QuizState) =
        _questionState.getAndUpdate(update)

    fun setQuestionEvent(even: IQuizContract.QuizUIEvent) =
        viewModelScope.launch { _questionEvent.emit(even) }

    init {
        getAllCats()
        observeLeftRightCatEvent()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                setQuestionState { copy(timer = timer - 1) }

                if (questionState.value.timer <= 0) {
                    pauseTimer()
                    addResult(
                        QuizResult(
                            result = seeResults(questionState.value.timer, questionState.value.points.toInt()),
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }
            }

        }
    }

    fun isCorrectAnswer(catId: String): Boolean {
        val questionIndex = questionState.value.questionIndex
        val question = questionState.value.questions[questionIndex]
        return catId == question.correctAnswer
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun getAllCats() {
        viewModelScope.launch {
            setQuestionState { copy(isLoading = true) }
            val cats = catService.getAllCatsFlow().first().shuffled()
            setQuestionState { copy(cats = cats) }
            createQuestions()
            setQuestionState { copy(isLoading = false) }
        }
    }

    private fun observeLeftRightCatEvent() {
        viewModelScope.launch {
            _questionEvent.collect {
                when (it) {
                    is IQuizContract.QuizUIEvent.QuestionAnswered -> checkAnswer(it.catAnswer)
                    IQuizContract.QuizUIEvent.Exit -> showQuizExitDialog()
                }
            }
        }
    }

    private fun addResult(result: QuizResult) {
        viewModelScope.launch {
            userDataStore.addResult(result)
            setQuestionState { copy(result = result) }
        }
    }

    private fun checkAnswer(catAnswer: Cat) {
        var questionIndex = questionState.value.questionIndex
        val question = questionState.value.questions[questionIndex]
        var points = questionState.value.points
        if (catAnswer.id == question.correctAnswer)
            points++

        if (questionIndex < 19)
            questionIndex++
        else { //End Screen
            pauseTimer()
            addResult(
                QuizResult(
                    result = seeResults(questionState.value.timer, points.toInt()),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        //delay(700) //delay for ripple animation to finish
        setQuestionState {
            copy(
                questionIndex = questionIndex,
                points = points,
            )
        }
    }

    private fun showQuizExitDialog() {
        setQuestionState { copy(showQuizExitDialog = !showQuizExitDialog) }
    }

    private suspend fun getAllPictures(id: String): List<String> {
        val photos = catService.getAllCatImagesByIdFlow(id = id).first()

        if (photos.isNotEmpty())
            return photos

        return withContext(dispatcherProvider.io()) {
            catService.getAllCatsPhotosApi(id = id).map { it.url }
        }
    }

    /**
     * Creates 20 random questions and saves them in a list
     */
    private suspend fun createQuestions() {
        val cats = questionState.value.cats
        val questions: MutableList<IQuizContract.QuizQuestion> = ArrayList()
        var skip = 0
        var i = 0
        var photoIndex = -1

        var cat1Photos = getAllPictures(cats[0].id)
        while (++i < 21 + skip) {
            photoIndex++

            val cat2Photos = getAllPictures(cats[i].id)
            if (cat2Photos.isEmpty()) {
                skip++
                continue
            }

            //If cat doesn't have images, it shouldn't be in the game
            if (cat1Photos.isEmpty()) {
                skip++
                cat1Photos = cat2Photos
                continue
            }

            val randomQuestion = Random.nextInt(1, 3)
            questions.add(
                IQuizContract.QuizQuestion(
                    cats = listOf(cats[i - 1], cats[i]),
                    images = listOf(
                        cat1Photos[photoIndex % cat1Photos.size],
                        cat2Photos[photoIndex % cat2Photos.size]
                    ),
                    questionText = giveQuestion(randomQuestion),
                    correctAnswer = giveAnswer(randomQuestion, cats[i - 1], cats[i])
                )
            )

            cat1Photos = cat2Photos
        }
        setQuestionState { copy(questions = questions.shuffled()) }
    }

    private fun giveQuestion(num: Int): String {
        return when (num) {
            1 -> "Which cat weights more on average?"
            else -> "Which cat has longer life span on average?"
        }
    }

    private fun giveAnswer(num: Int, cat1: Cat, cat2: Cat): String {
        return when (num) {
            1 -> if (cat1.averageWeight() > cat2.averageWeight()) cat1.id else cat2.id
            else -> if (cat1.averageLife() > cat2.averageLife()) cat1.id else cat2.id
        }
    }
}
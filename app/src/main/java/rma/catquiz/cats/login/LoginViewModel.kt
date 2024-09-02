package rma.catquiz.cats.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rma.catquiz.di.DispatcherProvider
import rma.catquiz.user.User
import rma.catquiz.user.UserDataStore
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val usersData: UserDataStore
) : ViewModel() {

    private val addNewUser = savedStateHandle.addNewUser
    private val _loginState = MutableStateFlow(ILoginContract.LoginState(addNewUser = addNewUser))
    val loginState = _loginState.asStateFlow()

    private val _loginEvents = MutableSharedFlow<ILoginContract.LoginUIEvent>()

    private val NICKNAME_PATTERN = Regex("[A-Za-z0-9_]+")

    private fun setLoginState(updateWith: ILoginContract.LoginState.() -> ILoginContract.LoginState) =
        _loginState.getAndUpdate(updateWith)

    fun setLoginEvent(event: ILoginContract.LoginUIEvent) = viewModelScope.launch {  _loginEvents.emit(event) }

    init {
        observerEvents()
    }

    private fun observerEvents() {
        viewModelScope.launch {
            _loginEvents.collect {
                when (it) {
                    is ILoginContract.LoginUIEvent.EmailInputChanged -> emailChange(it.email)
                    is ILoginContract.LoginUIEvent.NameInputChanged -> nameChange(it.name)
                    is ILoginContract.LoginUIEvent.NicknameInputChanged -> nicknameChange(it.nickname)
                    is ILoginContract.LoginUIEvent.AddUser -> addUser()
                }
            }
        }
    }

    fun isInfoValid(): Boolean {
        if (loginState.value.name.isEmpty())
            return false
        if (loginState.value.nickname.isEmpty() || !NICKNAME_PATTERN.matches(loginState.value.nickname))
            return false
        if (loginState.value.email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(loginState.value.email).matches())
            return false
        return true
    }

    private fun addUser() {
        val user = User(name = loginState.value.name, nickname = loginState.value.nickname, email = loginState.value.email)

        viewModelScope.launch {
            usersData.addUser(user)
            setLoginState { copy(loginCheckPassed = true) }
        }
    }


    private fun emailChange(email: String) {
        viewModelScope.launch {
            setLoginState { copy(email = email) }
        }
    }

    private fun nameChange(name: String) {
        viewModelScope.launch {
            setLoginState { copy(name = name) }
        }
    }

    private fun nicknameChange(nickname: String) {
        viewModelScope.launch {
            setLoginState { copy(nickname = nickname) }
        }
    }

    //TODO
    fun hasAccount(): Boolean {
        return usersData.data.value != null
    }
}

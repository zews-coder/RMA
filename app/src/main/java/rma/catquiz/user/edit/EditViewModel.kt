package rma.catquiz.user.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rma.catquiz.di.DispatcherProvider
import rma.catquiz.user.UserDataStore
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val usersData: UserDataStore
) : ViewModel() {

    private val _editState = MutableStateFlow(
        IEditContract.EditState(
        name = usersData.data.value.name,
        nickname = usersData.data.value.nickname,
        email = usersData.data.value.email
    ))
    val editState = _editState.asStateFlow()

    private val _editEvents = MutableSharedFlow<IEditContract.EditUIEvent>()

    private fun setEditState(updateWith: IEditContract.EditState.() -> IEditContract.EditState) =
        _editState.getAndUpdate(updateWith)

    fun setEditEvent(event: IEditContract.EditUIEvent) =
        viewModelScope.launch { _editEvents.emit(event) }

    //osluskuje koji se Event desio
    init {
        observerEvents()
    }


    private fun observerEvents() {
        viewModelScope.launch {
            _editEvents.collect {
                when (it) {
                    is IEditContract.EditUIEvent.EmailInputChanged -> emailChange(it.email)
                    is IEditContract.EditUIEvent.NameInputChanged -> nameChange(it.name)
                    is IEditContract.EditUIEvent.NicknameInputChanged -> nicknameChange(it.nickname)
                    is IEditContract.EditUIEvent.SaveChanges -> updateUser()
                }
            }
        }
    }

    fun isInfoValid(): Boolean {
        if (editState.value.name.isEmpty())
            return false
        if (editState.value.nickname.isEmpty())
            return false
        if (editState.value.email.isEmpty())
            return false
        return true
    }

    private fun updateUser() {
        var user = usersData.data.value

        user = user.copy(
            name = editState.value.name,
            nickname = editState.value.nickname,
            email = editState.value.email,
        )

        viewModelScope.launch {
            usersData.addUser(user)
            setEditState { copy(saveUserPassed = true) }
        }
    }

    private fun emailChange(email: String) {
        viewModelScope.launch {
            setEditState { copy(email = email) }
        }
    }

    private fun nameChange(name: String) {
        viewModelScope.launch {
            setEditState { copy(name = name) }
        }
    }

    private fun nicknameChange(nickname: String) {
        viewModelScope.launch {
            setEditState { copy(nickname = nickname) }
        }
    }
}
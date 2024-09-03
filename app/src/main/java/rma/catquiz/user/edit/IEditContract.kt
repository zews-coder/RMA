package rma.catquiz.user.edit

import android.graphics.Bitmap
import android.widget.ImageView

interface IEditContract {
    data class EditState (
        val nickname: String,
        val name: String,
        val email: String,
        val bitmap: Bitmap? = null,
        val saveUserPassed: Boolean = false
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }

    sealed class EditUIEvent{
        data class NicknameInputChanged(val nickname: String) : EditUIEvent()
        data class EmailInputChanged(val email: String) : EditUIEvent()
        data class NameInputChanged(val name: String) :  EditUIEvent()
        data object SaveChanges :  EditUIEvent()
    }
}
package com.example.takemynote.TakeMyNote.model

import android.os.Parcelable
import com.example.takemynote.TakeMyNote.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    var id: String = "",
    var description: String = "",
    var status: Int = 0
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}

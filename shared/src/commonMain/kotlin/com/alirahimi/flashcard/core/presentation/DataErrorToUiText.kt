package com.alirahimi.flashcard.core.presentation

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.presentation.resources.Res
import com.alirahimi.flashcard.core.presentation.resources.error_disk_full
import com.alirahimi.flashcard.core.presentation.resources.error_no_internet
import com.alirahimi.flashcard.core.presentation.resources.error_not_found
import com.alirahimi.flashcard.core.presentation.resources.error_server
import com.alirahimi.flashcard.core.presentation.resources.error_unauthorized
import com.alirahimi.flashcard.core.presentation.resources.error_unknown

fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Local.DISK_FULL -> UiText.StringResourceId(Res.string.error_disk_full)
        DataError.Local.NOT_FOUND -> UiText.StringResourceId(Res.string.error_not_found)
        DataError.Local.UNKNOWN -> UiText.StringResourceId(Res.string.error_unknown)
        DataError.Network.NO_INTERNET -> UiText.StringResourceId(Res.string.error_no_internet)
        DataError.Network.UNAUTHORIZED -> UiText.StringResourceId(Res.string.error_unauthorized)
        DataError.Network.SERVER_ERROR -> UiText.StringResourceId(Res.string.error_server)
        DataError.Network.NOT_FOUND -> UiText.StringResourceId(Res.string.error_not_found)
        else -> UiText.StringResourceId(Res.string.error_unknown)
    }
}

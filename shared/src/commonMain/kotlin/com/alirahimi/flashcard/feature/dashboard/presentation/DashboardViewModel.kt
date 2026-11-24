package com.alirahimi.flashcard.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.core.designsystem.theme.ThemeController
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.onFailure
import com.alirahimi.flashcard.core.domain.onSuccess
import com.alirahimi.flashcard.core.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val cardLocalDataSource: CardLocalDataSource,
    private val timeProvider: TimeProvider,
    private val themeController: ThemeController
) : ViewModel() {

    private val _state = MutableStateFlow(
        DashboardState(
            themeMode = themeController.themeConfig.value.mode,
            customPrimaryColor = themeController.themeConfig.value.customPrimaryColor
        )
    )
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _events = Channel<DashboardEvent>()
    val events = _events.receiveAsFlow()

    init {
        themeController.themeConfig
            .onEach { config ->
                _state.update {
                    it.copy(
                        themeMode = config.mode,
                        customPrimaryColor = config.customPrimaryColor
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.OnRefresh -> refreshDashboard()
            DashboardAction.OnStartReviewClick -> sendEvent(DashboardEvent.NavigateToReview)
            DashboardAction.OnAddCardClick -> sendEvent(DashboardEvent.NavigateToAddCard)
            DashboardAction.OnWordListClick -> sendEvent(DashboardEvent.NavigateToCardList)
            DashboardAction.OnImportClick -> sendEvent(DashboardEvent.NavigateToImport)
            DashboardAction.OnOpenThemeDialog -> _state.update { it.copy(isThemeDialogVisible = true) }
            DashboardAction.OnDismissThemeDialog -> _state.update { it.copy(isThemeDialogVisible = false) }
            is DashboardAction.OnThemeModeSelect -> themeController.setThemeMode(action.mode)
            is DashboardAction.OnCustomColorSelect -> themeController.setCustomPrimaryColor(action.color)
        }
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val now = timeProvider.currentTimeEpochMs()
            var countsByBox: Map<Int, Int> = emptyMap()
            var dueCount = 0

            cardLocalDataSource.getAllCards()
                .onSuccess { cards ->
                    countsByBox = cards.groupingBy { it.box }.eachCount()
                }
                .onFailure { error ->
                    val uiText = error.toUiText()
                    _state.update { it.copy(isLoading = false, error = uiText) }
                    _events.send(DashboardEvent.ShowError(uiText))
                    return@launch
                }

            cardLocalDataSource.getCardsForReview(now)
                .onSuccess { dueCards ->
                    dueCount = dueCards.size
                }
                .onFailure { error ->
                    val uiText = error.toUiText()
                    _state.update { it.copy(isLoading = false, error = uiText) }
                    _events.send(DashboardEvent.ShowError(uiText))
                    return@launch
                }

            _state.update {
                it.copy(
                    boxes = DashboardState.defaultBoxes(countsByBox),
                    dueCardsCount = dueCount,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    private fun sendEvent(event: DashboardEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }
}

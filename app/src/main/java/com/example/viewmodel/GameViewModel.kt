package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ComboSystem
import com.example.data.GameRepository
import com.example.data.GameStateEntity
import com.example.ui.Formatters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

enum class GameTab {
    TAP,
    POINT_SHOP,
    CASH_SHOP,
    RESTARTS
}

data class FloatingPopup(
    val id: Long,
    val text: String,
    val offsetXFraction: Float,
    val comboCount: Int = 0
)

data class ToastBanner(
    val id: Long,
    val message: String,
    val isError: Boolean
)

data class GameUiState(
    val isLoading: Boolean = true,
    val game: GameStateEntity = GameStateEntity(),
    val selectedTab: GameTab = GameTab.TAP,
    val isCooldownActive: Boolean = false,
    val cooldownRemainingFraction: Float = 0f,
    val comboCount: Int = 0,
    val comboMultiplier: Double = 1.0,
    val comboRemainingFraction: Float = 0f,
    val comboTierLabel: String = ComboSystem.tierLabelFor(0),
    val floatingPopups: List<FloatingPopup> = emptyList(),
    val toastBanner: ToastBanner? = null,
    val showAutoUpgradeSheet: Boolean = false,
    val showRestartDialog: Boolean = false,
    val autoTickCounter: Long = 0L
)

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null
    private var comboTimerJob: Job? = null
    private var toastJob: Job? = null
    private var nextPopupId = 1L
    private var nextToastId = 1L

    @Volatile
    private var isDirty = false

    init {
        viewModelScope.launch {
            val loaded = repository.loadOrCreate()
            _uiState.update { it.copy(isLoading = false, game = loaded) }
            startAutoWorker()
            startPeriodicSaveLoop()
        }
    }

    private fun startAutoWorker() {
        viewModelScope.launch {
            while (isActive) {
                val snapshot = _uiState.value.game
                if (snapshot.autoUnlocked && snapshot.autoEnabled) {
                    val delayMs = (snapshot.autoCooldownSec * 1000.0).roundToLong().coerceAtLeast(50L)
                    delay(delayMs)
                    val latest = _uiState.value.game
                    if (latest.autoUnlocked && latest.autoEnabled) {
                        val gained = latest.effectiveAutoPointsPerTick
                        _uiState.update { state ->
                            state.copy(
                                game = state.game.copy(
                                    diem = state.game.diem + gained,
                                    clicks = state.game.clicks + 1L
                                ),
                                autoTickCounter = state.autoTickCounter + 1L
                            )
                        }
                        isDirty = true
                    }
                } else {
                    delay(100L)
                }
            }
        }
    }

    private fun startPeriodicSaveLoop() {
        viewModelScope.launch {
            while (isActive) {
                delay(1500L)
                if (isDirty) {
                    isDirty = false
                    repository.saveState(_uiState.value.game)
                }
            }
        }
    }

    fun flushSave() {
        val current = _uiState.value
        if (!current.isLoading && isDirty) {
            isDirty = false
            viewModelScope.launch {
                repository.saveState(current.game)
            }
        }
    }

    fun selectTab(tab: GameTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                showAutoUpgradeSheet = false,
                showRestartDialog = false
            )
        }
    }

    fun onTapCore() {
        val current = _uiState.value
        if (current.isLoading || current.isCooldownActive) return

        val newCombo = current.comboCount + 1
        val comboMult = ComboSystem.multiplierFor(newCombo)
        val comboLabel = ComboSystem.tierLabelFor(newCombo)
        val gained = current.game.effectivePointsPerClick * comboMult
        val popupId = nextPopupId++
        val offsetFraction = ((popupId % 5) - 2) * 0.14f
        val popupText = if (comboMult > 1.0) {
            "+${Formatters.formatDecimal(gained)} (x${Formatters.formatDecimal(comboMult)})"
        } else {
            "+${Formatters.formatDecimal(gained)}"
        }

        _uiState.update { state ->
            val updatedPopups = (state.floatingPopups + FloatingPopup(
                id = popupId,
                text = popupText,
                offsetXFraction = offsetFraction,
                comboCount = newCombo
            )).takeLast(6)

            state.copy(
                game = state.game.copy(
                    diem = state.game.diem + gained,
                    clicks = state.game.clicks + 1L,
                    bestCombo = maxOf(state.game.bestCombo, newCombo)
                ),
                isCooldownActive = true,
                cooldownRemainingFraction = 1f,
                comboCount = newCombo,
                comboMultiplier = comboMult,
                comboRemainingFraction = 1f,
                comboTierLabel = comboLabel,
                floatingPopups = updatedPopups
            )
        }
        isDirty = true

        viewModelScope.launch {
            delay(650L)
            _uiState.update { state ->
                state.copy(floatingPopups = state.floatingPopups.filterNot { it.id == popupId })
            }
        }

        comboTimerJob?.cancel()
        comboTimerJob = viewModelScope.launch {
            val windowMs = maxOf(
                ComboSystem.COMBO_WINDOW_MS,
                (current.game.clickCooldownSec * 1000.0).roundToLong() + 1200L
            )
            val comboStart = System.currentTimeMillis()
            while (isActive) {
                val elapsed = System.currentTimeMillis() - comboStart
                val remaining = (windowMs - elapsed).coerceAtLeast(0L)
                if (remaining == 0L) break
                _uiState.update {
                    it.copy(comboRemainingFraction = remaining.toFloat() / windowMs.toFloat())
                }
                delay(32L)
            }
            _uiState.update {
                it.copy(
                    comboCount = 0,
                    comboMultiplier = 1.0,
                    comboRemainingFraction = 0f,
                    comboTierLabel = ComboSystem.tierLabelFor(0)
                )
            }
        }

        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            val totalMs = (current.game.clickCooldownSec * 1000.0).roundToLong().coerceAtLeast(15L)
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val remaining = (totalMs - elapsed).coerceAtLeast(0L)
                if (remaining == 0L) break
                _uiState.update {
                    it.copy(cooldownRemainingFraction = remaining.toFloat() / totalMs.toFloat())
                }
                delay(16L)
            }
            _uiState.update {
                it.copy(isCooldownActive = false, cooldownRemainingFraction = 0f)
            }
        }
    }

    fun toggleAutoEnabled() {
        val current = _uiState.value.game
        if (!current.autoUnlocked) return
        val nextEnabled = !current.autoEnabled
        updateAndPersist(current.copy(autoEnabled = nextEnabled))
        showToast(
            if (nextEnabled) "Đã bật Auto Click" else "Đã tạm dừng Auto Click",
            isError = false
        )
    }

    // --- Point Shop (Shop 1) ---
    fun buyPointPower() {
        val g = _uiState.value.game
        val cost = g.discountedPointCost(g.pointPowerCost)
        if (g.diem >= cost) {
            updateAndPersist(
                g.copy(
                    diem = (g.diem - cost).coerceAtLeast(0.0),
                    pointsPerClick = g.pointsPerClick * 2.0,
                    pointPowerCost = g.pointPowerCost * 3.0,
                    pointPowerLv = g.pointPowerLv + 1
                )
            )
            showToast("Mua nâng cấp +Điểm/Click thành công!", isError = false)
        } else {
            val needed = Formatters.formatPoints(cost - g.diem)
            showToast("Cần thêm $needed điểm để mua nâng cấp", isError = true)
        }
    }

    fun buyMultiplier() {
        val g = _uiState.value.game
        val cost = g.discountedPointCost(g.multiplierCost)
        if (g.diem >= cost) {
            val newMult = ((g.multiplier + 0.1) * 100.0).roundToLong() / 100.0
            updateAndPersist(
                g.copy(
                    diem = (g.diem - cost).coerceAtLeast(0.0),
                    multiplier = newMult,
                    multiplierCost = g.multiplierCost * 3.0,
                    multiplierLv = g.multiplierLv + 1
                )
            )
            showToast("Tăng hệ số nhân lên ${Formatters.formatMultiplier(newMult)}!", isError = false)
        } else {
            val needed = Formatters.formatPoints(cost - g.diem)
            showToast("Cần thêm $needed điểm để mua nâng cấp", isError = true)
        }
    }

    fun buyCooldownReduction() {
        val g = _uiState.value.game
        val cost = g.discountedPointCost(g.cooldownCost)
        if (g.diem >= cost) {
            val newCd = (g.clickCooldownSec - g.cooldownStep).coerceAtLeast(0.01)
            updateAndPersist(
                g.copy(
                    diem = (g.diem - cost).coerceAtLeast(0.0),
                    clickCooldownSec = newCd,
                    cooldownStep = g.cooldownStep / 2.0,
                    cooldownCost = g.cooldownCost * 3.0,
                    cooldownLv = g.cooldownLv + 1
                )
            )
            showToast("Giảm hồi chiêu xuống ${Formatters.formatSeconds(newCd)}!", isError = false)
        } else {
            val needed = Formatters.formatPoints(cost - g.diem)
            showToast("Cần thêm $needed điểm để mua nâng cấp", isError = true)
        }
    }

    // --- Exchange & $Shop$ (Shop 3) ---
    fun exchange1000Points() {
        val g = _uiState.value.game
        if (g.diem >= 1000.0) {
            updateAndPersist(
                g.copy(
                    diem = (g.diem - 1000.0).coerceAtLeast(0.0),
                    dollars = g.dollars + 1.0
                )
            )
            showToast("Đổi 1.000 điểm → $1 thành công!", isError = false)
        } else {
            val needed = Formatters.formatPoints(1000.0 - g.diem)
            showToast("Chưa đủ điểm! Cần thêm $needed điểm", isError = true)
        }
    }

    fun exchangeAllPoints() {
        val g = _uiState.value.game
        if (g.diem > 0.0) {
            val gained = g.diem / 1000.0
            val newTotal = g.dollars + gained
            updateAndPersist(
                g.copy(
                    diem = 0.0,
                    dollars = newTotal
                )
            )
            showToast(
                "Đã đổi toàn bộ điểm (+${Formatters.formatDollars(gained)})!",
                isError = false
            )
        } else {
            showToast("Bạn chưa có điểm nào để quy đổi", isError = true)
        }
    }

    fun buyDiscount() {
        val g = _uiState.value.game
        if (g.dollars >= g.discountCost) {
            val newFactor = g.discountFactor + g.discountStep
            updateAndPersist(
                g.copy(
                    dollars = (g.dollars - g.discountCost).coerceAtLeast(0.0),
                    discountCost = g.discountCost * 3.0,
                    discountFactor = newFactor,
                    discountStep = g.discountStep * 2.0,
                    discountLv = g.discountLv + 1
                )
            )
            showToast("Mua Giảm giá thành công (÷${Formatters.formatDecimal(newFactor)})!", isError = false)
        } else {
            val needed = Formatters.formatDollars(g.discountCost - g.dollars)
            showToast("Thiếu $needed để mua Giảm giá", isError = true)
        }
    }

    fun buyMenuSpeed() {
        val g = _uiState.value.game
        if (g.dollars >= g.menuSpeedCost) {
            val newMenuSec = (g.menuTransitionSec / 2.0).coerceAtLeast(0.05)
            updateAndPersist(
                g.copy(
                    dollars = (g.dollars - g.menuSpeedCost).coerceAtLeast(0.0),
                    menuTransitionSec = newMenuSec,
                    menuSpeedCost = g.menuSpeedCost * 3.0,
                    menuSpeedLv = g.menuSpeedLv + 1
                )
            )
            showToast("Tăng tốc chuyển cảnh (${Formatters.formatSeconds(newMenuSec)})!", isError = false)
        } else {
            val needed = Formatters.formatDollars(g.menuSpeedCost - g.dollars)
            showToast("Thiếu $needed để mua vật phẩm", isError = true)
        }
    }

    fun buyToastSpeed() {
        val g = _uiState.value.game
        if (g.dollars >= g.toastSpeedCost) {
            val newToastSec = (g.toastDurationSec / 2.0).coerceAtLeast(0.15)
            updateAndPersist(
                g.copy(
                    dollars = (g.dollars - g.toastSpeedCost).coerceAtLeast(0.0),
                    toastDurationSec = newToastSec,
                    toastSpeedCost = g.toastSpeedCost * 3.0,
                    toastSpeedLv = g.toastSpeedLv + 1
                )
            )
            showToast("Giảm thời gian thông báo (${Formatters.formatSeconds(newToastSec)})!", isError = false)
        } else {
            val needed = Formatters.formatDollars(g.toastSpeedCost - g.dollars)
            showToast("Thiếu $needed để mua vật phẩm", isError = true)
        }
    }

    fun buyAutoClick() {
        val g = _uiState.value.game
        if (g.autoUnlocked) {
            showToast("Bạn đã mua Auto Click rồi!", isError = true)
            return
        }
        val price = 25.0
        if (g.dollars >= price) {
            updateAndPersist(
                g.copy(
                    dollars = (g.dollars - price).coerceAtLeast(0.0),
                    autoUnlocked = true,
                    autoEnabled = true
                )
            )
            showToast("Đã kích hoạt Auto Click!", isError = false)
        } else {
            val needed = Formatters.formatDollars(price - g.dollars)
            showToast("Thiếu $needed để mở khóa Auto Click", isError = true)
        }
    }

    fun openAutoUpgradeSheet() {
        _uiState.update { it.copy(showAutoUpgradeSheet = true) }
    }

    fun dismissAutoUpgradeSheet() {
        _uiState.update { it.copy(showAutoUpgradeSheet = false) }
    }

    fun buyAutoPower() {
        val g = _uiState.value.game
        if (!g.autoUnlocked) {
            showToast("Cần mở khóa Auto Click trước", isError = true)
            return
        }
        if (g.dollars >= g.autoPowerCost) {
            val newAutoPoints = g.autoPoints * 2.0
            updateAndPersist(
                g.copy(
                    dollars = (g.dollars - g.autoPowerCost).coerceAtLeast(0.0),
                    autoPoints = newAutoPoints,
                    autoPowerCost = g.autoPowerCost * 3.0,
                    autoPowerLv = g.autoPowerLv + 1
                )
            )
            showToast("Tăng điểm Auto lên ${Formatters.formatDecimal(newAutoPoints)}!", isError = false)
        } else {
            val needed = Formatters.formatDollars(g.autoPowerCost - g.dollars)
            showToast("Thiếu $needed để nâng cấp sức mạnh Auto", isError = true)
        }
    }

    fun buyAutoSpeed() {
        val g = _uiState.value.game
        if (!g.autoUnlocked) {
            showToast("Cần mở khóa Auto Click trước", isError = true)
            return
        }
        if (g.dollars >= g.autoSpeedCost) {
            val newAutoCd = (g.autoCooldownSec - g.autoSpeedStep).coerceAtLeast(0.05)
            updateAndPersist(
                g.copy(
                    dollars = (g.dollars - g.autoSpeedCost).coerceAtLeast(0.0),
                    autoCooldownSec = newAutoCd,
                    autoSpeedStep = g.autoSpeedStep / 2.0,
                    autoSpeedCost = g.autoSpeedCost * 3.0,
                    autoSpeedLv = g.autoSpeedLv + 1
                )
            )
            showToast("Tăng tốc Auto còn ${Formatters.formatSeconds(newAutoCd)}!", isError = false)
        } else {
            val needed = Formatters.formatDollars(g.autoSpeedCost - g.dollars)
            showToast("Thiếu $needed để nâng cấp tốc độ Auto", isError = true)
        }
    }

    // --- Restarts (Prestige) ---
    fun requestRestartDialog() {
        val g = _uiState.value.game
        val req = g.restartRequirement
        if (g.diem >= req) {
            _uiState.update { it.copy(showRestartDialog = true) }
        } else {
            val needed = Formatters.formatPoints(req - g.diem)
            showToast("Chưa đủ điểm để Restarts! Cần thêm $needed điểm", isError = true)
        }
    }

    fun dismissRestartDialog() {
        _uiState.update { it.copy(showRestartDialog = false) }
    }

    fun confirmRestart() {
        val g = _uiState.value.game
        val req = g.restartRequirement
        if (g.diem < req) {
            _uiState.update { it.copy(showRestartDialog = false) }
            val needed = Formatters.formatPoints(req - g.diem)
            showToast("Cần thêm $needed điểm để Restarts", isError = true)
            return
        }

        comboTimerJob?.cancel()
        val newBaseMultiplier = ((g.baseMultiplier + 0.5) * 100.0).roundToLong() / 100.0
        val resetState = GameStateEntity(
            id = 1,
            diem = 0.0,
            dollars = 0.0,
            clicks = 0L,
            multiplier = newBaseMultiplier,
            baseMultiplier = newBaseMultiplier,
            pointsPerClick = 1.0,
            clickCooldownSec = 0.25,
            cooldownStep = 0.1,
            discountFactor = 1.0,
            discountStep = 0.1,
            pointPowerLv = 0,
            pointPowerCost = 25.0,
            multiplierLv = 0,
            multiplierCost = 50.0,
            cooldownLv = 0,
            cooldownCost = 75.0,
            discountLv = 0,
            discountCost = 5.0,
            menuSpeedLv = 0,
            menuSpeedCost = 3.0,
            menuTransitionSec = 1.0,
            toastSpeedLv = 0,
            toastSpeedCost = 3.0,
            toastDurationSec = 1.0,
            autoUnlocked = false,
            autoEnabled = false,
            autoPoints = 1.0,
            autoCooldownSec = 1.0,
            autoPowerLv = 0,
            autoPowerCost = 15.0,
            autoSpeedLv = 0,
            autoSpeedCost = 20.0,
            autoSpeedStep = 0.25,
            restartLv = g.restartLv + 1,
            restartCost = g.restartCost * 3.0,
            bestCombo = g.bestCombo
        )

        _uiState.update {
            it.copy(
                game = resetState,
                showRestartDialog = false,
                isCooldownActive = false,
                cooldownRemainingFraction = 0f,
                comboCount = 0,
                comboMultiplier = 1.0,
                comboRemainingFraction = 0f,
                comboTierLabel = ComboSystem.tierLabelFor(0),
                selectedTab = GameTab.TAP
            )
        }
        isDirty = false
        viewModelScope.launch {
            repository.saveState(resetState)
        }
        showToast(
            "Restarts thành công! Hệ số nhân khởi điểm: ${Formatters.formatMultiplier(newBaseMultiplier)}",
            isError = false
        )
    }

    private fun updateAndPersist(newState: GameStateEntity) {
        _uiState.update { it.copy(game = newState) }
        isDirty = false
        viewModelScope.launch {
            repository.saveState(newState)
        }
    }

    private fun showToast(message: String, isError: Boolean) {
        val toastId = nextToastId++
        _uiState.update {
            it.copy(toastBanner = ToastBanner(id = toastId, message = message, isError = isError))
        }
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            val durationMs = (_uiState.value.game.toastDurationSec * 1000.0)
                .roundToLong()
                .coerceAtLeast(250L)
            delay(durationMs)
            _uiState.update { state ->
                if (state.toastBanner?.id == toastId) {
                    state.copy(toastBanner = null)
                } else {
                    state
                }
            }
        }
    }

    class Factory(private val repository: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameViewModel(repository) as T
        }
    }
}

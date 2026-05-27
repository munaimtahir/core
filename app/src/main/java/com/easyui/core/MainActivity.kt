package com.easyui.core

import android.app.role.RoleManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Build
import android.os.BatteryManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.easyui.core.apps.AppDiscovery
import com.easyui.core.apps.AppIconLoader
import com.easyui.core.apps.AppLauncher
import com.easyui.core.apps.LaunchResult
import com.easyui.core.apps.LaunchableApp
import com.easyui.core.apps.PackageManagerAppDiscovery
import com.easyui.core.apps.PackageManagerAppIconLoader
import com.easyui.core.apps.PackageManagerAppLauncher
import com.easyui.core.home.AppComponentRef
import com.easyui.core.home.HomeGridSpec
import com.easyui.core.home.HomeLayout
import com.easyui.core.home.HomeLayoutRepository
import com.easyui.core.home.HomeSlotId
import com.easyui.core.onboarding.OnboardingRepository
import com.easyui.core.theme.IconSize
import com.easyui.core.theme.TextSize
import com.easyui.core.theme.ThemePalette
import com.easyui.core.theme.ThemeRepository
import com.easyui.core.theme.ThemeSettings
import com.easyui.core.ui.theme.CoreTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow

private enum class LaunchOrigin {
    LauncherIcon,
    HomeLauncher,
}

class MainActivity : ComponentActivity() {
    private val launchOriginFlow = MutableStateFlow(LaunchOrigin.LauncherIcon)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLaunchOrigin(intent)
        setContent { AppEntry(launchOriginFlow) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        updateLaunchOrigin(intent)
    }

    private fun updateLaunchOrigin(intent: Intent?) {
        launchOriginFlow.value = classifyLaunchOrigin(intent)
    }

    private fun classifyLaunchOrigin(intent: Intent?): LaunchOrigin {
        val action = intent?.action
        val categories = intent?.categories.orEmpty()
        return when {
            action == Intent.ACTION_MAIN &&
                categories.contains(Intent.CATEGORY_HOME) -> LaunchOrigin.HomeLauncher
            else -> LaunchOrigin.LauncherIcon
        }
    }
}

private sealed interface Screen {
    data object Onboarding : Screen
    data object Home : Screen
    data object AllApps : Screen
    data object CustomizeHome : Screen
    data object ThemeSettings : Screen
    data object StatusDebug : Screen
    data object ResetOptions : Screen
}

private enum class OnboardingStep {
    Launcher,
    Appearance,
    Layout,
}

@Composable
private fun AppEntry(launchOriginFlow: MutableStateFlow<LaunchOrigin>) {
    val context = LocalContext.current
    val launchOrigin by launchOriginFlow.collectAsState()
    val themeRepo = remember { ThemeRepository(context) }
    val themeSettings by themeRepo.settingsFlow.collectAsState(initial = ThemeSettings())

    CoreTheme(settings = themeSettings) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppRoot(
                themeRepo = themeRepo,
                themeSettings = themeSettings,
                launchOrigin = launchOrigin,
            )
        }
    }
}

@Composable
private fun AppRoot(
    themeRepo: ThemeRepository,
    themeSettings: ThemeSettings,
    launchOrigin: LaunchOrigin,
) {
    val context = LocalContext.current
    val spec = remember { HomeGridSpec(pageCount = 3, columns = 3, rows = 3) }
    val homeRepo = remember { HomeLayoutRepository(context, spec) }
    val onboardingRepo = remember { OnboardingRepository(context) }

    val discovery: AppDiscovery = remember { PackageManagerAppDiscovery(context) }
    val launcher: AppLauncher = remember { PackageManagerAppLauncher(context) }
    val iconLoader: AppIconLoader = remember { PackageManagerAppIconLoader(context) }

    val homeIconSize = remember(themeSettings.iconSize) { homeIconDp(themeSettings.iconSize) }
    val listIconSize = remember(themeSettings.iconSize) { listIconDp(themeSettings.iconSize) }

    var screen by remember {
        mutableStateOf(
            when (launchOrigin) {
                LaunchOrigin.HomeLauncher -> Screen.Home
                LaunchOrigin.LauncherIcon -> Screen.Onboarding
            }
        )
    }
    var lastError by remember { mutableStateOf<String?>(null) }

    var apps by remember { mutableStateOf<List<LaunchableApp>>(emptyList()) }
    var appsLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Launcher back-button policy:
    // - On Home: consume Back so the launcher doesn't "exit" to an empty wallpaper screen.
    // - On other screens: Back returns to Home.
    BackHandler(enabled = true) {
        when (screen) {
            Screen.Onboarding -> Unit
            Screen.Home -> Unit
            else -> screen = Screen.Home
        }
    }

    suspend fun refreshApps() {
        appsLoading = true
        apps = withContext(Dispatchers.IO) { discovery.discoverLaunchableApps() }
        appsLoading = false
    }

    LaunchedEffect(Unit) { refreshApps() }
    val layout by homeRepo.layoutFlow.collectAsState(initial = HomeLayout(spec = spec))
    val pageCount = layout.spec.pageCount

    when (screen) {
        Screen.Onboarding -> OnboardingScreen(
            selected = themeSettings,
            pageCount = pageCount,
            onSetPalette = { palette -> scope.launch { themeRepo.setPalette(palette) } },
            onSetTextSize = { size -> scope.launch { themeRepo.setTextSize(size) } },
            onSetIconSize = { size -> scope.launch { themeRepo.setIconSize(size) } },
            onIncreasePages = { scope.launch { homeRepo.increasePageCount() } },
            onDecreasePages = { scope.launch { homeRepo.decreasePageCount() } },
            onOpenPlacementEditor = { screen = Screen.CustomizeHome },
            onFinish = {
                scope.launch { onboardingRepo.markCompleted() }
                screen = Screen.Home
            },
        )

        Screen.Home -> HomeScreen(
            spec = spec,
            pageCount = pageCount,
            layout = layout,
            apps = apps,
            isRefreshingApps = appsLoading,
            iconLoader = iconLoader,
            iconSize = homeIconSize,
            onRefreshApps = {
                lastError = null
                scope.launch { refreshApps() }
            },
            onLaunch = { app ->
                when (val result = launcher.launch(app)) {
                    is LaunchResult.Success -> Unit
                    is LaunchResult.Failure -> lastError = result.message
                }
            },
            lastError = lastError,
            onOpenAllApps = { lastError = null; screen = Screen.AllApps },
            onOpenStatus = { lastError = null; screen = Screen.StatusDebug },
            onOpenReset = { lastError = null; screen = Screen.ResetOptions },
        )

        Screen.AllApps -> AllAppsScreen(
            apps = apps,
            isLoading = appsLoading,
            iconLoader = iconLoader,
            iconSize = listIconSize,
            onBack = { screen = Screen.Home },
            onRefresh = {
                lastError = null
                scope.launch { refreshApps() }
            },
            onLaunch = { app ->
                when (val result = launcher.launch(app)) {
                    is LaunchResult.Success -> Unit
                    is LaunchResult.Failure -> {
                        lastError = result.message
                        screen = Screen.Home
                    }
                }
            },
        )

        Screen.CustomizeHome -> CustomizeHomeScreen(
            spec = spec,
            pageCount = pageCount,
            layout = layout,
            apps = apps,
            isLoadingApps = appsLoading,
            iconLoader = iconLoader,
            iconSize = homeIconSize,
            onBack = { screen = Screen.Home },
            onSetSlot = { slot, ref ->
                scope.launch { homeRepo.setSlot(slot, ref) }
            },
            onMove = { from, to ->
                scope.launch {
                    val fromRef = layout.get(from)
                    val toRef = layout.get(to)
                    homeRepo.setSlot(from, toRef)
                    homeRepo.setSlot(to, fromRef)
                }
            },
            onResetHome = { scope.launch { homeRepo.resetHome() } },
            onRefreshApps = { scope.launch { refreshApps() } },
        )

        Screen.ThemeSettings -> ThemeSettingsScreen(
            selected = themeSettings,
            onBack = { screen = Screen.Home },
            onSetPalette = { palette -> scope.launch { themeRepo.setPalette(palette) } },
            onSetTextSize = { size -> scope.launch { themeRepo.setTextSize(size) } },
            onSetIconSize = { size -> scope.launch { themeRepo.setIconSize(size) } },
        )

        Screen.StatusDebug -> StatusDebugScreen(
            themeSettings = themeSettings,
            appCount = apps.size,
            homeSlotsCount = layout.filledSlotsCount(),
            onBack = { screen = Screen.Home },
        )

        Screen.ResetOptions -> ResetOptionsScreen(
            onBack = { screen = Screen.Home },
            onResetHomeOnly = { scope.launch { homeRepo.resetHome() } },
            onResetAll = {
                scope.launch {
                    homeRepo.resetHome()
                    homeRepo.setPageCount(spec.pageCount)
                    themeRepo.resetToDefault()
                }
            },
        )
    }
}

@Composable
private fun OnboardingScreen(
    selected: ThemeSettings,
    pageCount: Int,
    onSetPalette: (ThemePalette) -> Unit,
    onSetTextSize: (TextSize) -> Unit,
    onSetIconSize: (IconSize) -> Unit,
    onIncreasePages: () -> Unit,
    onDecreasePages: () -> Unit,
    onOpenPlacementEditor: () -> Unit,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var step by remember { mutableStateOf(OnboardingStep.Launcher) }
    var refreshTick by remember { mutableIntStateOf(0) }

    val status = remember(refreshTick) { computeLauncherStatus(context) }

    val launcherForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            scope.launch {
                repeat(6) {
                    refreshTick++
                    delay(300)
                }
            }
        },
    )

    fun openDefaultHomePicker() {
        val intent = createDefaultHomePickerIntent(context)
        if (intent != null) launcherForResult.launch(intent)
        else refreshTick++
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("onboarding_screen"),
    ) {
        Text(
            text = when (step) {
                OnboardingStep.Launcher -> stringResource(R.string.onboarding_step_launcher_title)
                OnboardingStep.Appearance -> stringResource(R.string.onboarding_step_appearance_title)
                OnboardingStep.Layout -> stringResource(R.string.onboarding_step_layout_title)
            },
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (step) {
                OnboardingStep.Launcher -> stringResource(R.string.onboarding_step_launcher_body)
                OnboardingStep.Appearance -> stringResource(R.string.onboarding_step_appearance_body)
                OnboardingStep.Layout -> stringResource(R.string.onboarding_step_layout_body)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (step) {
            OnboardingStep.Launcher -> {
                Text(text = stringResource(R.string.onboarding_current_launcher), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = status, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_open_default_launcher"),
                    onClick = { openDefaultHomePicker() },
                ) {
                    Text(text = stringResource(R.string.onboarding_choose_default_launcher))
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_refresh_status"),
                    onClick = { refreshTick++ },
                ) {
                    Text(text = stringResource(R.string.refresh))
                }
            }

            OnboardingStep.Appearance -> {
                ThemeSettingsContent(
                    selected = selected,
                    onSetPalette = onSetPalette,
                    onSetTextSize = onSetTextSize,
                    onSetIconSize = onSetIconSize,
                    showIconSize = false,
                )
            }

            OnboardingStep.Layout -> {
                Text(text = stringResource(R.string.onboarding_page_count_label), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.onboarding_page_count_value, pageCount), style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f).testTag("onboarding_decrease_pages"),
                        enabled = pageCount > 1,
                        onClick = onDecreasePages,
                    ) {
                        Text(text = stringResource(R.string.page_decrease))
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f).testTag("onboarding_increase_pages"),
                        enabled = pageCount < 5,
                        onClick = onIncreasePages,
                    ) {
                        Text(text = stringResource(R.string.page_increase))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenPlacementEditor,
                ) {
                    Text(text = stringResource(R.string.onboarding_open_placement_editor))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f).testTag("onboarding_back"),
                enabled = step != OnboardingStep.Launcher,
                onClick = {
                    step = when (step) {
                        OnboardingStep.Launcher -> OnboardingStep.Launcher
                        OnboardingStep.Appearance -> OnboardingStep.Launcher
                        OnboardingStep.Layout -> OnboardingStep.Appearance
                    }
                },
            ) {
                Text(text = stringResource(R.string.back))
            }

            Button(
                modifier = Modifier.weight(1f).testTag("onboarding_next"),
                onClick = {
                    step = when (step) {
                        OnboardingStep.Launcher -> OnboardingStep.Appearance
                        OnboardingStep.Appearance -> OnboardingStep.Layout
                        OnboardingStep.Layout -> {
                            onFinish()
                            OnboardingStep.Layout
                        }
                    }
                },
            ) {
                Text(
                    text = when (step) {
                        OnboardingStep.Layout -> stringResource(R.string.onboarding_finish)
                        else -> stringResource(R.string.next)
                    },
                )
            }
        }
    }
}

private fun createDefaultHomePickerIntent(context: Context): Intent? {
    // Best-effort: request ROLE_HOME on Android 10+ where supported.
    if (Build.VERSION.SDK_INT >= 29) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
            return roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
        }
    }
    // Fallback: open system UI where the user can change default Home app.
    return Intent(Settings.ACTION_HOME_SETTINGS)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreen(
    spec: HomeGridSpec,
    pageCount: Int,
    layout: HomeLayout,
    apps: List<LaunchableApp>,
    isRefreshingApps: Boolean,
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    onRefreshApps: () -> Unit,
    onLaunch: (LaunchableApp) -> Unit,
    lastError: String?,
    onOpenAllApps: () -> Unit,
    onOpenStatus: () -> Unit,
    onOpenReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageIndex by remember { mutableStateOf(0) }
    LaunchedEffect(pageCount) { pageIndex = pageIndex.coerceAtMost(pageCount - 1) }
    val clockState = rememberClockState()
    val networkStatusState = rememberNetworkStatusState()

    val appMap = remember(apps) {
        apps.associateBy { "${it.packageName}|${it.activityName}" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("home_screen"),
    ) {
        HomeClockStrip(clockState = clockState)

        Spacer(modifier = Modifier.height(12.dp))

        HomeStatusStrip(status = networkStatusState)

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.testTag("home_all_apps_button"),
                onClick = onOpenAllApps,
            ) {
                Text(text = stringResource(R.string.open_all_apps))
            }
        }

        if (lastError != null) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = lastError,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("home_page_indicator"),
        ) {
            Text(
                text = stringResource(R.string.page_indicator, pageIndex + 1, spec.pageCount),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isRefreshingApps) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            }
            Text(
                modifier = Modifier
                    .clickable(onClick = onRefreshApps)
                    .padding(8.dp),
                text = stringResource(R.string.refresh),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val density = LocalDensity.current
        val swipeThresholdPx = with(density) { 48.dp.toPx() }
        var dragAccumPx by remember { mutableStateOf(0f) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(spec.columns),
            modifier = Modifier
                .weight(1f)
                .testTag("home_page_${pageIndex + 1}")
                .pointerInput(pageIndex) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            // Don't block vertical scrolling; just track horizontal intent.
                            dragAccumPx += dragAmount
                            change.consume()
                        },
                        onDragEnd = {
                            val next = when {
                                dragAccumPx <= -swipeThresholdPx -> (pageIndex + 1).coerceAtMost(spec.pageCount - 1)
                                dragAccumPx >= swipeThresholdPx -> (pageIndex - 1).coerceAtLeast(0)
                                else -> pageIndex
                            }
                            dragAccumPx = 0f
                    pageIndex = next.coerceAtMost(pageCount - 1)
                },
                        onDragCancel = { dragAccumPx = 0f },
                    )
                },
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(items = layout.pages[pageIndex]) { slotIndex, ref ->
                val resolved = ref?.let { appMap["${it.packageName}|${it.activityName}"] }

                HomeTile(
                    modifier = Modifier.testTag("home_slot_page_${pageIndex}_slot_$slotIndex"),
                    iconLoader = iconLoader,
                    iconSize = iconSize,
                    app = resolved,
                    showEmptyPlaceholder = false,
                    isUnavailable = ref != null && resolved == null,
                    onClick = {
                        if (resolved != null) onLaunch(resolved)
                    },
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onOpenStatus,
            ) {
                Text(text = stringResource(R.string.status_debug))
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onOpenReset,
            ) {
                Text(text = stringResource(R.string.reset_options))
            }
        }
    }
}

private data class ClockState(
    val timeText: String,
    val dateText: String,
    val monthDayText: String,
)

@Composable
private fun rememberClockState(): ClockState {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val timeFormatter = remember(locale) {
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, locale)
    }
    val dateFormatter = remember(locale) {
        SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, locale)
    }
    val monthDayFormatter = remember(locale) {
        SimpleDateFormat("MMMM d", locale)
    }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            val delayMs = 60_000L - (nowMillis % 60_000L)
            delay(delayMs.coerceIn(1_000L, 60_000L))
        }
    }

    return remember(nowMillis, timeFormatter, dateFormatter, monthDayFormatter) {
        val now = Date(nowMillis)
        ClockState(
            timeText = timeFormatter.format(now),
            dateText = dateFormatter.format(now),
            monthDayText = monthDayFormatter.format(now),
        )
    }
}

@Composable
private fun HomeClockStrip(clockState: ClockState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 112.dp)
            .testTag("home_clock_strip"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = clockState.timeText,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = clockState.dateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = clockState.monthDayText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private data class NetworkStatusState(
    val simLabel: String,
    val signalText: String,
    val connectionText: String,
    val networkNameText: String,
    val batteryText: String,
)

@Composable
private fun rememberNetworkStatusState(): NetworkStatusState {
    val context = LocalContext.current
    var now by remember { mutableStateOf(buildNetworkStatusState(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            now = buildNetworkStatusState(context)
            delay(15_000L)
        }
    }

    return now
}

private fun buildNetworkStatusState(context: Context): NetworkStatusState {
    val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    val wifiManager = context.getSystemService(WifiManager::class.java)
    val telephonyManager = context.getSystemService(TelephonyManager::class.java)

    val activeNetwork = connectivityManager?.activeNetwork
    val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
    val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    val isCellular = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    val isValidated = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    val isConnected = hasInternet && isValidated

    val simCount = when {
        telephonyManager == null -> 0
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> telephonyManager.activeModemCount
        else -> 1
    }
    val simLabel = when {
        simCount > 1 -> "SIM1 · SIM2"
        simCount == 1 -> "SIM1"
        else -> "SIM"
    }

    val signalText = when {
        isWifi -> {
            val level = wifiManager?.connectionInfo?.let { info ->
                WifiManager.calculateSignalLevel(info.rssi, 5) + 1
            }
            if (level != null) "Signal $level/5" else "Signal Wi‑Fi"
        }
        isCellular -> "Signal mobile"
        else -> "Signal unavailable"
    }

    val carrierName = telephonyManager?.networkOperatorName?.takeIf { it.isNotBlank() }
    val wifiName = wifiManager?.connectionInfo?.ssid
        ?.takeIf { !it.isNullOrBlank() && it != WifiManager.UNKNOWN_SSID }
        ?.trim('"')
    val connectionText = when {
        isWifi -> if (isConnected) "Connected · Wi‑Fi" else "Wi‑Fi"
        isCellular -> if (isConnected) "Connected · Mobile data" else "Mobile data"
        else -> "Offline"
    }
    val networkNameText = when {
        isWifi -> wifiName ?: "Wi‑Fi"
        isCellular -> carrierName ?: "Mobile data"
        else -> "No network"
    }

    val batteryText = readBatteryPercentage(context)?.let { "$it%" } ?: "Battery --"

    return NetworkStatusState(
        simLabel = simLabel,
        signalText = signalText,
        connectionText = connectionText,
        networkNameText = networkNameText,
        batteryText = batteryText,
    )
}

private fun readBatteryPercentage(context: Context): Int? {
    val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return null
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    if (level < 0 || scale <= 0) return null
    return ((level * 100f) / scale).toInt().coerceIn(0, 100)
}

@Composable
private fun HomeStatusStrip(status: NetworkStatusState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .testTag("home_status_strip"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${status.simLabel} · ${status.signalText}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = status.batteryText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status.connectionText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = status.networkNameText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeTile(
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    app: LaunchableApp?,
    showEmptyPlaceholder: Boolean,
    isUnavailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when {
        app != null -> app.label
        isUnavailable -> stringResource(R.string.unavailable_app)
        showEmptyPlaceholder -> stringResource(R.string.empty_slot)
        else -> ""
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = app != null, onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AndroidView(
            modifier = Modifier.size(iconSize),
            factory = { ctx ->
                ImageView(ctx).apply { importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO }
            },
            update = { view ->
                if (app != null) {
                    view.setImageDrawable(iconLoader.loadIcon(app))
                } else if (showEmptyPlaceholder || isUnavailable) {
                    view.setImageResource(R.drawable.ic_app_placeholder)
                } else {
                    view.setImageDrawable(null)
                }
            },
        )
        if (title.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun AllAppsScreen(
    apps: List<LaunchableApp>,
    isLoading: Boolean,
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onLaunch: (LaunchableApp) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("all_apps_screen"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.all_apps_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp),
                text = stringResource(R.string.back),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .clickable(onClick = onRefresh)
                    .padding(8.dp)
                    .testTag("all_apps_refresh"),
                text = stringResource(R.string.refresh),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(R.string.loading_apps),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            return
        }

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_apps_found),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            return
        }

        LazyColumn(modifier = Modifier.testTag("all_apps_list")) {
            items(items = apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                AppRow(app = app, iconLoader = iconLoader, iconSize = iconSize, onClick = { onLaunch(app) })
            }
        }
    }
}

@Composable
private fun AppRow(
    app: LaunchableApp,
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AndroidView(
            modifier = Modifier.size(iconSize),
            factory = { ctx ->
                ImageView(ctx).apply {
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                    isClickable = false
                    isFocusable = false
                    isFocusableInTouchMode = false
                    // Ensure scroll gestures starting on the icon are not consumed by the View.
                    setOnTouchListener { _, _ -> false }
                }
            },
            update = { view ->
                view.setImageDrawable(iconLoader.loadIcon(app))
            },
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun CustomizeHomeScreen(
    spec: HomeGridSpec,
    pageCount: Int,
    layout: HomeLayout,
    apps: List<LaunchableApp>,
    isLoadingApps: Boolean,
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    onBack: () -> Unit,
    onSetSlot: (HomeSlotId, AppComponentRef?) -> Unit,
    onMove: (HomeSlotId, HomeSlotId) -> Unit,
    onResetHome: () -> Unit,
    onRefreshApps: () -> Unit,
) {
    var pageIndex by remember { mutableStateOf(0) }
    LaunchedEffect(pageCount) {
        pageIndex = pageIndex.coerceAtMost(pageCount - 1)
    }
    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }
    var moveFrom by remember { mutableStateOf<HomeSlotId?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val appMap = remember(apps) { apps.associateBy { "${it.packageName}|${it.activityName}" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("customize_home_screen"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.customize_home), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp)
                    .testTag("customize_back_home"),
                text = stringResource(R.string.back),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.testTag("customize_page_selector")) {
            for (p in 0 until pageCount) {
                val label = stringResource(R.string.page_short, p + 1)
                OutlinedButton(
                    modifier = Modifier.testTag("customize_page_${p + 1}"),
                    onClick = {
                        pageIndex = p
                        selectedSlotIndex = null
                    },
                ) {
                    Text(text = label)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(spec.columns),
            modifier = Modifier
                .height(320.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(items = layout.pages[pageIndex]) { i, ref ->
                val slot = HomeSlotId(pageIndex, i)
                val resolved = ref?.let { appMap["${it.packageName}|${it.activityName}"] }
                val isSelected = selectedSlotIndex == i
                val isMoveSource = moveFrom == slot

                Column(
                    modifier = Modifier
                        .testTag("customize_slot_page_${pageIndex}_slot_$i")
                        .aspectRatio(1f)
                        .clickable {
                            selectedSlotIndex = i
                            if (moveFrom != null && moveFrom != slot) {
                                // destination selection completes move
                            }
                        }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    AndroidView(
                        modifier = Modifier.size(iconSize),
                        factory = { ctx -> ImageView(ctx).apply { importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO } },
                        update = { view ->
                            if (resolved != null) {
                                view.setImageDrawable(iconLoader.loadIcon(resolved))
                            } else {
                                view.setImageResource(R.drawable.ic_app_placeholder)
                            }
                        },
                    )
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = when {
                            resolved != null -> resolved.label
                            ref != null -> stringResource(R.string.unavailable_app)
                            else -> stringResource(R.string.empty_slot)
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (isSelected) {
                        Text(
                            modifier = Modifier.testTag("customize_selected_slot"),
                            text = stringResource(R.string.selected),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    } else if (isMoveSource) {
                        Text(
                            text = stringResource(R.string.moving),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f).testTag("customize_remove_slot"),
                enabled = selectedSlotIndex != null,
                onClick = {
                    val idx = selectedSlotIndex ?: return@OutlinedButton
                    val slot = HomeSlotId(pageIndex, idx)
                    moveFrom = null
                    onSetSlot(slot, null)
                },
            ) {
                Text(text = stringResource(R.string.remove))
            }
            OutlinedButton(
                modifier = Modifier.weight(1f).testTag("customize_move_start"),
                enabled = selectedSlotIndex != null,
                onClick = {
                    val idx = selectedSlotIndex ?: return@OutlinedButton
                    moveFrom = HomeSlotId(pageIndex, idx)
                },
            ) {
                Text(text = stringResource(R.string.move))
            }
            OutlinedButton(
                modifier = Modifier.weight(1f).testTag("customize_reset_home"),
                onClick = { showResetConfirm = true },
            ) {
                Text(text = stringResource(R.string.reset_home))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoadingApps) {
            Text(text = stringResource(R.string.loading_apps), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = stringResource(R.string.tap_app_to_assign),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .testTag("customize_app_picker"),
        ) {
            items(items = apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                AppRow(
                    app = app,
                    iconLoader = iconLoader,
                    iconSize = iconSize,
                    onClick = {
                        val idx = selectedSlotIndex ?: return@AppRow
                        val slot = HomeSlotId(pageIndex, idx)
                        val ref = AppComponentRef(app.packageName, app.activityName)
                        moveFrom = null
                        onSetSlot(slot, ref)
                    },
                )
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(text = stringResource(R.string.reset_home)) },
            text = { Text(text = stringResource(R.string.reset_home_confirm)) },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag("reset_confirm"),
                    onClick = {
                        showResetConfirm = false
                        moveFrom = null
                        selectedSlotIndex = null
                        onResetHome()
                    },
                ) { Text(text = stringResource(R.string.reset)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirm = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }

    LaunchedEffect(moveFrom, selectedSlotIndex, pageIndex) {
        val from = moveFrom ?: return@LaunchedEffect
        val destIdx = selectedSlotIndex ?: return@LaunchedEffect
        val to = HomeSlotId(pageIndex, destIdx)
        if (from != to) {
            onMove(from, to)
            moveFrom = null
        }
    }

    LaunchedEffect(Unit) { onRefreshApps() }
}

@Composable
private fun ThemeSettingsScreen(
    selected: ThemeSettings,
    onBack: () -> Unit,
    onSetPalette: (ThemePalette) -> Unit,
    onSetTextSize: (TextSize) -> Unit,
    onSetIconSize: (IconSize) -> Unit,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("theme_settings_screen"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.theme_settings), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
                text = stringResource(R.string.back),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Bring the text-size controls into view on small screens.
                // The scroll state is part of the same column, so this is predictable.
                scope.launch {
                    scrollState.animateScrollTo(scrollState.maxValue / 2)
                }
            },
        ) {
            Text(text = stringResource(R.string.theme_text_jump_label))
        }

        Spacer(modifier = Modifier.height(12.dp))

        ThemeSettingsContent(
            selected = selected,
            onSetPalette = onSetPalette,
            onSetTextSize = onSetTextSize,
            onSetIconSize = onSetIconSize,
            showIconSize = true,
        )
    }
}

@Composable
private fun ThemeSettingsContent(
    selected: ThemeSettings,
    onSetPalette: (ThemePalette) -> Unit,
    onSetTextSize: (TextSize) -> Unit,
    onSetIconSize: (IconSize) -> Unit,
    showIconSize: Boolean = true,
) {
    Text(text = stringResource(R.string.theme_section_palette), color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(6.dp))

    ThemeOptionRow(
        tag = "theme_palette_system",
        label = stringResource(R.string.theme_palette_system),
        selected = selected.palette == ThemePalette.System,
        previewSettings = selected.copy(palette = ThemePalette.System),
        onClick = { onSetPalette(ThemePalette.System) },
    )
    ThemeOptionRow(
        tag = "theme_palette_light",
        label = stringResource(R.string.theme_palette_light),
        selected = selected.palette == ThemePalette.Light,
        previewSettings = selected.copy(palette = ThemePalette.Light),
        onClick = { onSetPalette(ThemePalette.Light) },
    )
    ThemeOptionRow(
        tag = "theme_palette_dark",
        label = stringResource(R.string.theme_palette_dark),
        selected = selected.palette == ThemePalette.Dark,
        previewSettings = selected.copy(palette = ThemePalette.Dark),
        onClick = { onSetPalette(ThemePalette.Dark) },
    )

    Spacer(modifier = Modifier.height(12.dp))
    Text(text = stringResource(R.string.theme_section_text), color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        modifier = Modifier.testTag("theme_text_scope_note"),
        text = stringResource(R.string.theme_text_scope_note),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(8.dp))

    ThemeOptionRow(
        tag = "theme_text_small",
        label = stringResource(R.string.theme_text_small),
        subtitle = stringResource(R.string.theme_text_small_desc),
        scaleLabel = stringResource(R.string.theme_text_scale_small),
        selected = selected.textSize == TextSize.Small,
        previewSettings = selected.copy(textSize = TextSize.Small),
        onClick = { onSetTextSize(TextSize.Small) },
    )
    ThemeOptionRow(
        tag = "theme_text_normal",
        label = stringResource(R.string.theme_text_normal),
        subtitle = stringResource(R.string.theme_text_normal_desc),
        scaleLabel = stringResource(R.string.theme_text_scale_normal),
        selected = selected.textSize == TextSize.Normal,
        previewSettings = selected.copy(textSize = TextSize.Normal),
        onClick = { onSetTextSize(TextSize.Normal) },
    )
    ThemeOptionRow(
        tag = "theme_text_large",
        label = stringResource(R.string.theme_text_large),
        subtitle = stringResource(R.string.theme_text_large_desc),
        scaleLabel = stringResource(R.string.theme_text_scale_large),
        selected = selected.textSize == TextSize.Large,
        previewSettings = selected.copy(textSize = TextSize.Large),
        onClick = { onSetTextSize(TextSize.Large) },
    )
    ThemeOptionRow(
        tag = "theme_text_larger",
        label = stringResource(R.string.theme_text_larger),
        subtitle = stringResource(R.string.theme_text_larger_desc),
        scaleLabel = stringResource(R.string.theme_text_scale_larger),
        selected = selected.textSize == TextSize.Larger,
        previewSettings = selected.copy(textSize = TextSize.Larger),
        onClick = { onSetTextSize(TextSize.Larger) },
    )

    if (showIconSize) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.theme_section_icons), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(6.dp))

        ThemeOptionRow(
            tag = "theme_icons_normal",
            label = stringResource(R.string.theme_icons_normal),
            selected = selected.iconSize == IconSize.Normal,
            previewSettings = selected.copy(iconSize = IconSize.Normal),
            onClick = { onSetIconSize(IconSize.Normal) },
        )
        ThemeOptionRow(
            tag = "theme_icons_large",
            label = stringResource(R.string.theme_icons_large),
            selected = selected.iconSize == IconSize.Large,
            previewSettings = selected.copy(iconSize = IconSize.Large),
            onClick = { onSetIconSize(IconSize.Large) },
        )
    }
}

@Composable
private fun ThemeOptionRow(
    tag: String,
    label: String,
    subtitle: String? = null,
    scaleLabel: String? = null,
    selected: Boolean,
    previewSettings: ThemeSettings,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .testTag(tag),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (scaleLabel != null) {
                Text(
                    text = scaleLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (selected) {
                Text(
                    modifier = Modifier.testTag("theme_selected_marker"),
                    text = stringResource(R.string.selected),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ThemePreviewCard(settings = previewSettings)
    }
}

@Composable
private fun ThemePreviewCard(settings: ThemeSettings) {
    CoreTheme(settings = settings) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            val icon = when (settings.iconSize) {
                IconSize.Large -> 36.dp
                IconSize.Normal -> 28.dp
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.theme_preview_title),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.theme_preview_scale, settings.textSize.scaleFactor),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(icon)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.theme_preview_row_primary),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = stringResource(R.string.theme_preview_row_secondary),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusDebugScreen(
    themeSettings: ThemeSettings,
    appCount: Int,
    homeSlotsCount: Int,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val launcherStatus = remember { computeLauncherStatus(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("status_debug_screen"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.status_debug), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
                text = stringResource(R.string.back),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatusRow(tag = "status_app_version", label = stringResource(R.string.status_app_version), value = BuildConfig.VERSION_NAME)
        StatusRow(
            tag = "status_launcher_state",
            label = stringResource(R.string.status_launcher_state),
            value = launcherStatus,
        )
        StatusRow(tag = "status_detected_app_count", label = stringResource(R.string.status_detected_app_count), value = appCount.toString())
        StatusRow(
            tag = "status_selected_theme",
            label = stringResource(R.string.status_selected_theme),
            value = "${themeSettings.palette.storageValue}, ${themeSettings.textSize.storageValue}, ${themeSettings.iconSize.storageValue}",
        )
        StatusRow(tag = "status_selected_home_slots_count", label = stringResource(R.string.status_selected_home_slots_count), value = homeSlotsCount.toString())
    }
}

private fun computeLauncherStatus(context: Context): String {
    // Best-effort only. Different OEMs/devices can behave differently.
    if (isCoreDefaultHome(context)) {
        return context.getString(R.string.launcher_state_is_default)
    }

    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_HOME)
        .addCategory(Intent.CATEGORY_DEFAULT)

    @Suppress("DEPRECATION")
    val ri = pm.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
    val pkg = ri?.activityInfo?.packageName

    return when {
        pkg == null -> context.getString(R.string.launcher_state_unknown)
        pkg == context.packageName -> context.getString(R.string.launcher_state_is_default)
        pkg == "android" -> context.getString(R.string.launcher_state_unknown)
        else -> context.getString(R.string.launcher_state_default_other, pkg)
    }
}

private fun isCoreDefaultHome(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= 29) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
            if (roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                return true
            }
        }
    }

    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_HOME)
        .addCategory(Intent.CATEGORY_DEFAULT)
    @Suppress("DEPRECATION")
    val ri = pm.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
    return ri?.activityInfo?.packageName == context.packageName
}

private fun homeIconDp(iconSize: IconSize): androidx.compose.ui.unit.Dp {
    return when (iconSize) {
        IconSize.Large -> 56.dp
        IconSize.Normal -> 44.dp
    }
}

private fun listIconDp(iconSize: IconSize): androidx.compose.ui.unit.Dp {
    return when (iconSize) {
        IconSize.Large -> 52.dp
        IconSize.Normal -> 40.dp
    }
}

@Composable
private fun StatusRow(tag: String, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp).testTag(tag)) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = value)
    }
}

@Composable
private fun ResetOptionsScreen(
    onBack: () -> Unit,
    onResetHomeOnly: () -> Unit,
    onResetAll: () -> Unit,
) {
    var confirm by remember { mutableStateOf<ResetKind?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reset_options_screen"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.reset_options), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
                text = stringResource(R.string.back),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.testTag("reset_home_only"),
            onClick = { confirm = ResetKind.HomeOnly },
        ) {
            Text(text = stringResource(R.string.reset_home_only))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.testTag("reset_all_settings"),
            onClick = { confirm = ResetKind.AllSettings },
        ) {
            Text(text = stringResource(R.string.reset_all_settings))
        }
    }

    if (confirm != null) {
        val kind = confirm!!
        AlertDialog(
            onDismissRequest = { confirm = null },
            title = { Text(text = stringResource(R.string.reset_confirm_title)) },
            text = {
                Text(
                    text = when (kind) {
                        ResetKind.HomeOnly -> stringResource(R.string.reset_home_only_confirm)
                        ResetKind.AllSettings -> stringResource(R.string.reset_all_settings_confirm)
                    },
                )
            },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag("reset_confirm"),
                    onClick = {
                        when (kind) {
                            ResetKind.HomeOnly -> onResetHomeOnly()
                            ResetKind.AllSettings -> onResetAll()
                        }
                        confirm = null
                    },
                ) { Text(text = stringResource(R.string.reset)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirm = null }) { Text(text = stringResource(R.string.cancel)) }
            },
        )
    }

    LaunchedEffect(confirm) {
        // no-op (confirmation is handled by dialog callbacks)
    }
}

private enum class ResetKind { HomeOnly, AllSettings }

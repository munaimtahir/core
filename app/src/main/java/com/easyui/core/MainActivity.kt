package com.easyui.core

import android.app.role.RoleManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppEntry() }
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

@Composable
private fun AppEntry() {
    val context = LocalContext.current
    val themeRepo = remember { ThemeRepository(context) }
    val themeSettings by themeRepo.settingsFlow.collectAsState(initial = ThemeSettings())

    CoreTheme(settings = themeSettings) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppRoot(themeRepo = themeRepo, themeSettings = themeSettings)
        }
    }
}

@Composable
private fun AppRoot(
    themeRepo: ThemeRepository,
    themeSettings: ThemeSettings,
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

    val onboardingCompleted by onboardingRepo.isCompletedFlow.collectAsState(initial = false)
    var screen by remember { mutableStateOf<Screen>(if (onboardingCompleted) Screen.Home else Screen.Onboarding) }
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
    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted && screen == Screen.Onboarding) {
            screen = Screen.Home
        }
    }

    val layout by homeRepo.layoutFlow.collectAsState(initial = HomeLayout(spec = spec))

    when (screen) {
        Screen.Onboarding -> OnboardingScreen(
            selected = themeSettings,
            onSetPalette = { palette -> scope.launch { themeRepo.setPalette(palette) } },
            onSetTextSize = { size -> scope.launch { themeRepo.setTextSize(size) } },
            onSetIconSize = { size -> scope.launch { themeRepo.setIconSize(size) } },
            onContinue = {
                scope.launch { onboardingRepo.markCompleted() }
                screen = Screen.Home
            },
        )

        Screen.Home -> HomeScreen(
            spec = spec,
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
            onOpenCustomize = { lastError = null; screen = Screen.CustomizeHome },
            onOpenTheme = { lastError = null; screen = Screen.ThemeSettings },
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
            onResetAll = { scope.launch { homeRepo.resetHome(); themeRepo.resetToDefault() } },
        )
    }
}

@Composable
private fun OnboardingScreen(
    selected: ThemeSettings,
    onSetPalette: (ThemePalette) -> Unit,
    onSetTextSize: (TextSize) -> Unit,
    onSetIconSize: (IconSize) -> Unit,
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    var refreshTick by remember { mutableIntStateOf(0) }

    val status = remember(refreshTick) { computeLauncherStatus(context) }

    val launcherForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { refreshTick++ },
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
        Text(text = stringResource(R.string.onboarding_title), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.onboarding_instructions),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.onboarding_settings_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ThemeSettingsContent(
            selected = selected,
            onSetPalette = onSetPalette,
            onSetTextSize = onSetTextSize,
            onSetIconSize = onSetIconSize,
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth().testTag("onboarding_continue"),
            onClick = onContinue,
        ) {
            Text(text = stringResource(R.string.continue_to_home))
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
    layout: HomeLayout,
    apps: List<LaunchableApp>,
    isRefreshingApps: Boolean,
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    onRefreshApps: () -> Unit,
    onLaunch: (LaunchableApp) -> Unit,
    lastError: String?,
    onOpenAllApps: () -> Unit,
    onOpenCustomize: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenStatus: () -> Unit,
    onOpenReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageIndex by remember { mutableStateOf(0) }

    val appMap = remember(apps) {
        apps.associateBy { "${it.packageName}|${it.activityName}" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("home_screen"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
                            pageIndex = next
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
                    isUnavailable = ref != null && resolved == null,
                    onClick = {
                        if (resolved != null) onLaunch(resolved)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f).testTag("home_customize_button"),
                onClick = onOpenCustomize,
            ) {
                Text(text = stringResource(R.string.customize_home))
            }
            OutlinedButton(
                modifier = Modifier.weight(1f).testTag("home_settings_button"),
                onClick = onOpenTheme,
            ) {
                Text(text = stringResource(R.string.theme_settings))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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

@Composable
private fun HomeTile(
    iconLoader: AppIconLoader,
    iconSize: androidx.compose.ui.unit.Dp,
    app: LaunchableApp?,
    isUnavailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when {
        app != null -> app.label
        isUnavailable -> stringResource(R.string.unavailable_app)
        else -> stringResource(R.string.empty_slot)
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
                if (app != null) view.setImageDrawable(iconLoader.loadIcon(app))
                else view.setImageResource(R.drawable.ic_app_placeholder)
            },
        )
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
            for (p in 0 until spec.pageCount) {
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
                            if (resolved != null) view.setImageDrawable(iconLoader.loadIcon(resolved))
                            else view.setImageResource(R.drawable.ic_app_placeholder)
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        ThemeSettingsContent(
            selected = selected,
            onSetPalette = onSetPalette,
            onSetTextSize = onSetTextSize,
            onSetIconSize = onSetIconSize,
        )
    }
}

@Composable
private fun ThemeSettingsContent(
    selected: ThemeSettings,
    onSetPalette: (ThemePalette) -> Unit,
    onSetTextSize: (TextSize) -> Unit,
    onSetIconSize: (IconSize) -> Unit,
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
    ThemeOptionRow(
        tag = "theme_palette_high_contrast",
        label = stringResource(R.string.theme_high_contrast),
        selected = selected.palette == ThemePalette.HighContrast,
        previewSettings = selected.copy(palette = ThemePalette.HighContrast),
        onClick = { onSetPalette(ThemePalette.HighContrast) },
    )

    Spacer(modifier = Modifier.height(12.dp))
    Text(text = stringResource(R.string.theme_section_text), color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(6.dp))

    ThemeOptionRow(
        tag = "theme_text_small",
        label = stringResource(R.string.theme_text_small),
        selected = selected.textSize == TextSize.Small,
        previewSettings = selected.copy(textSize = TextSize.Small),
        onClick = { onSetTextSize(TextSize.Small) },
    )
    ThemeOptionRow(
        tag = "theme_text_normal",
        label = stringResource(R.string.theme_text_normal),
        selected = selected.textSize == TextSize.Normal,
        previewSettings = selected.copy(textSize = TextSize.Normal),
        onClick = { onSetTextSize(TextSize.Normal) },
    )
    ThemeOptionRow(
        tag = "theme_text_large",
        label = stringResource(R.string.theme_text_large),
        selected = selected.textSize == TextSize.Large,
        previewSettings = selected.copy(textSize = TextSize.Large),
        onClick = { onSetTextSize(TextSize.Large) },
    )
    ThemeOptionRow(
        tag = "theme_text_larger",
        label = stringResource(R.string.theme_text_larger),
        selected = selected.textSize == TextSize.Larger,
        previewSettings = selected.copy(textSize = TextSize.Larger),
        onClick = { onSetTextSize(TextSize.Larger) },
    )

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

@Composable
private fun ThemeOptionRow(
    tag: String,
    label: String,
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
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
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
    if (Build.VERSION.SDK_INT >= 29) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
            if (roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                return context.getString(R.string.launcher_state_is_default)
            }
        }
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

package com.easyui.core

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.easyui.core.apps.AppIconLoader
import com.easyui.core.apps.LaunchResult
import com.easyui.core.apps.LaunchableApp
import com.easyui.core.apps.PackageManagerAppDiscovery
import com.easyui.core.apps.PackageManagerAppIconLoader
import com.easyui.core.apps.PackageManagerAppLauncher
import com.easyui.core.ui.theme.CoreTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppRoot()
                }
            }
        }
    }
}

private sealed interface Screen {
    data object Home : Screen
    data object AppList : Screen
}

@Composable
private fun AppRoot() {
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var lastError by remember { mutableStateOf<String?>(null) }

    when (screen) {
        Screen.Home -> HomeScreen(
            lastError = lastError,
            onOpenAllApps = {
                lastError = null
                screen = Screen.AppList
            },
        )

        Screen.AppList -> AppListScreen(
            onBack = { screen = Screen.Home },
            onLaunchError = { message -> lastError = message },
        )
    }
}

@Composable
private fun HomeScreen(
    lastError: String?,
    onOpenAllApps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (lastError != null) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = lastError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Button(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onOpenAllApps,
        ) {
            Text(text = stringResource(R.string.open_all_apps))
        }
    }
}

@Composable
private fun AppListScreen(
    onBack: () -> Unit,
    onLaunchError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val discovery = remember { PackageManagerAppDiscovery(context) }
    val launcher = remember { PackageManagerAppLauncher(context) }
    val iconLoader = remember { PackageManagerAppIconLoader(context) }

    var apps by remember { mutableStateOf<List<LaunchableApp>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        apps = withContext(Dispatchers.IO) { discovery.discoverLaunchableApps() }
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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

        LazyColumn {
            items(items = apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                AppRow(
                    app = app,
                    iconLoader = iconLoader,
                    onClick = {
                        when (val result = launcher.launch(app)) {
                            is LaunchResult.Success -> Unit
                            is LaunchResult.Failure -> {
                                onLaunchError(result.message)
                                onBack()
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun AppRow(
    app: LaunchableApp,
    iconLoader: AppIconLoader,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AndroidView(
            modifier = Modifier.size(40.dp),
            factory = { ctx ->
                ImageView(ctx).apply {
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
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

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    CoreTheme {
        HomeScreen(lastError = null, onOpenAllApps = {})
    }
}

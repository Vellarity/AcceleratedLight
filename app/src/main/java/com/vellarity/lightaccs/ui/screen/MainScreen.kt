package com.vellarity.lightaccs.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vellarity.lightaccs.data.interactor.FlashlightInteractor
import com.vellarity.lightaccs.ui.component.FabSettingsButton
import com.vellarity.lightaccs.ui.component.LightButton
import com.vellarity.lightaccs.ui.theme.DarkGrey
import com.vellarity.lightaccs.ui.theme.Purple80

@Composable
fun MainScreenRoot() {
    val context = LocalContext.current.applicationContext

    val viewModel: MainScreenViewModel = viewModel {
        MainScreenViewModel(context)
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.VIBRATE
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        if (!allGranted) {

        }
    }

    LaunchedEffect(Unit) {
        val needsRequest = permissions.any {
            context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (needsRequest) {
            launcher.launch(permissions)
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle().value

    MainScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainScreenState,
    onAction: (MainScreenAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkGrey,
        floatingActionButton = {
            FabSettingsButton(
                onClick = {showBottomSheet = true}
            )
        },
    ) { innerPadding ->
        if (showBottomSheet) {
            SettingsBottomSheet(
                sheetState = sheetState,
                onAction = {},
                onDismissRequest = {showBottomSheet = false}
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(30.dp)
        ) {
            LightButton(
                isLightOn = state.isLight,
                onClick = {
                    onAction(MainScreenAction.ToggleLight)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsBottomSheet(
    sheetState: SheetState,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sliderPosition by remember { mutableFloatStateOf(12f) }
    var isServiceWork by remember { mutableStateOf(true) }

    ModalBottomSheet(
        containerColor = DarkGrey,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Purple80)},
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Фоновая работа сервиса: ")
                    Switch(
                        checked = isServiceWork,
                        onCheckedChange = {isServiceWork = it},
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Purple80
                        )
                    )
                }
                Column {
                    Text("Сила трясучки: ")
                    Slider(
                        value = sliderPosition,
                        valueRange = 4f..20f,
                        onValueChange = { sliderPosition = it },
                        steps = 15,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Purple80,
                            activeTickColor = DarkGrey,
                            thumbColor = Purple80
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Проще")
                        Text("Сложнее")
                    }
                }
            }

    }
}

@Preview
@Composable
fun MainScreenPreview() {
    val state = MainScreenState(
        isLight = false,
        isShakeOn = false,
    )

    MainScreen(state = state, onAction = {})
}
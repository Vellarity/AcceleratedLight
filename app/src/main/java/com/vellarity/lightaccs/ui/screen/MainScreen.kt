package com.vellarity.lightaccs.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vellarity.lightaccs.data.interactor.FlashlightInteractor
import com.vellarity.lightaccs.ui.component.LightButton
import com.vellarity.lightaccs.ui.theme.DarkGrey

@Composable
fun MainScreenRoot() {
    val context = LocalContext.current.applicationContext

    // 2. Создаем ViewModel через Factory
    // Функция viewModel() сама позаботится о сохранении состояния при переворотах
    val viewModel: MainScreenViewModel = viewModel {
        MainScreenViewModel(context)
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(Manifest.permission.CAMERA)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // Здесь можно проверить, дал ли пользователь разрешения
        val allGranted = permissionsMap.values.all { it }
        if (!allGranted) {
            // Обработка отказа (например, показать Toast, что фонарик не будет работать)
        }
    }

    // Запрашиваем разрешения при первом запуске экрана
    LaunchedEffect(Unit) {
        // Проверяем, есть ли уже разрешения
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

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainScreenState,
    onAction: (MainScreenAction) -> Unit
) {
    Scaffold(
        containerColor = DarkGrey
    ) { innerPadding ->
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
                    print(state.isLight)
                    onAction(MainScreenAction.ToggleLight)
                }
            )
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
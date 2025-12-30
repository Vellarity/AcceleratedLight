package com.vellarity.lightaccs.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vellarity.lightaccs.R
import com.vellarity.lightaccs.ui.theme.DarkGrey
import com.vellarity.lightaccs.ui.theme.PurpleGrey40
import com.vellarity.lightaccs.ui.theme.SecondPurple

@Composable
fun FabSettingsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(70.dp)
            .shadow(10.dp),
        containerColor = SecondPurple
    ) {
        Image(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = null,
            colorFilter = ColorFilter.tint(DarkGrey)
        )
    }
}

@Preview
@Composable
fun FabSettingsSuttonPreview() {
    FabSettingsButton()
}
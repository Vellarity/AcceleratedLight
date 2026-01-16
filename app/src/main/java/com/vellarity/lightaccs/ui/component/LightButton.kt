package com.vellarity.lightaccs.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vellarity.lightaccs.ui.theme.DarkGrey
import com.vellarity.lightaccs.ui.theme.PrimePurple
import com.vellarity.lightaccs.ui.theme.Purple80
import com.vellarity.lightaccs.ui.theme.SecondPurple

@Composable
fun LightButton(
    modifier: Modifier = Modifier,
    isLightOn: Boolean = false,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    var color = if (isLightOn) Purple80 else SecondPurple

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = color, RoundedCornerShape(100))
                .padding(20.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DarkGrey, RoundedCornerShape(100))
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .width(20.dp)
                        .align(Alignment.TopCenter)
                        .background(color = color, RoundedCornerShape(100))
                        .padding(5.dp)
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
fun LightButtonPreview() {
    LightButton(isLightOn = true)
}
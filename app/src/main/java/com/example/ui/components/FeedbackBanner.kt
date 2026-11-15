package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.CoralCrimsonDim
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldDim
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.ToastBanner

@Composable
fun FeedbackBanner(
    banner: ToastBanner?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = banner != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        if (banner != null) {
            val accent = if (banner.isError) CoralCrimson else NeonEmerald
            val container = if (banner.isError) CoralCrimsonDim else NeonEmeraldDim

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feedback_banner"),
                shape = RoundedCornerShape(14.dp),
                color = container,
                border = BorderStroke(1.dp, accent.copy(alpha = 0.6f)),
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (banner.isError) Icons.Rounded.WarningAmber else Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = banner.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

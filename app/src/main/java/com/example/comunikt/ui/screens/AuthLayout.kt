
package com.example.comunikt.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthLayout(
    title: String,
    description: String,
    onBack: (() -> Unit)? = null,
    backEnabled: Boolean = true,
    centerContent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = if (centerContent) {
            Arrangement.Center
        } else {
            Arrangement.Top
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.secondary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "C",
                    color = colors.onSecondary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column {
                Text(
                    text = "ComuniKT",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Comunicación sin barreras",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (onBack != null) {
            TextButton(
                onClick = onBack,
                enabled = backEnabled,
            ) {
                Text("← Volver")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = colors.surface,
            ),
            border = BorderStroke(
                width = 1.dp,
                color = colors.outlineVariant,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

package dev.bragas.timecapsule.ui.screen.capsule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.bragas.timecapsule.model.Capsule
import dev.bragas.timecapsule.utils.Utils
import java.time.OffsetDateTime

@Composable
fun CapsuleItem(
    modifier: Modifier = Modifier,
    capsule: Capsule,
    onClick: () -> Unit,
) {
    val (icon: ImageVector, iconDescription: String) = when {
        OffsetDateTime.parse(capsule.readableAt)
            .isAfter(OffsetDateTime.now()) -> Icons.Outlined.Lock to "Lock"

        !capsule.isRead -> Icons.Outlined.MarkEmailUnread to "Unread"
        else -> Icons.Outlined.MarkEmailRead to "Read"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            Arrangement.SpaceBetween
        ) {
            Text("Date: ${Utils.utcToLocalDate(capsule.readableAt)}")
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
        }
    }
}
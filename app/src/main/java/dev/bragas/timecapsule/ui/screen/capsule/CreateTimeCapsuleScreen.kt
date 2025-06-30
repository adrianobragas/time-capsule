package dev.bragas.timecapsule.ui.screen.capsule

import AdvancedTimePickerExample
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.bragas.timecapsule.ui.common.DatePickerModal
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimeCapsuleScreen(
    navController: NavController,
    capsuleViewModel: CapsuleViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onCapsuleCreated: () -> Unit
) {
    var userSeleted by remember { mutableStateOf("") }
    var dateSelected by remember { mutableStateOf("") }
    var timeSelected by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    val recipient by capsuleViewModel.recipient.collectAsState()
    val message by capsuleViewModel.message.collectAsState()
    val users by capsuleViewModel.users.collectAsState()
    val context = LocalContext.current
    val timeFieldInteraction = remember { MutableInteractionSource() }

    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(timeFieldInteraction) {
        timeFieldInteraction.interactions.collectLatest { interaction ->
            if (interaction is PressInteraction.Release) {
                showTimePicker = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() })
                    {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retourner")
                    }
                },
                title = { Text(text = "Creer une capsule") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { isExpanded = !isExpanded },
                modifier = Modifier
            ) {
                OutlinedTextField(
                    value = userSeleted,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Destinataire") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.email) },
                            onClick = {
                                userSeleted = user.email
                                capsuleViewModel.onRecipientChange(user.uid)
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DatePickerModal(
                    value = dateSelected,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .weight(1f),
                    onDateSelected = { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis.toEpochMilli())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        dateSelected =
                            selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        capsuleViewModel.onReadableAtChange(millis.toString())
                    }
                )

                OutlinedTextField(
                    value = timeSelected,
                    onValueChange = {},
                    label = { Text("Heure") },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f),
                    trailingIcon = {
                        Icon(Icons.Default.AccessTime, contentDescription = null)
                    },
                    singleLine = true,
                    interactionSource = timeFieldInteraction,
                )
            }

            if (showTimePicker) {
                AdvancedTimePickerExample(
                    onConfirm = { state ->
                        val hour = state.hour
                        val minute = state.minute
                        timeSelected = "%02d:%02d".format(hour, minute)

                        val localDate = if (dateSelected.isNotEmpty()) {
                            LocalDate.parse(
                                dateSelected,
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )
                        } else {
                            LocalDate.now()
                        }

                        val localDateTime = LocalDateTime.of(localDate, LocalTime.of(hour, minute))
                        val utcZoned = localDateTime
                            .atZone(ZoneId.systemDefault())
                            .withZoneSameInstant(ZoneOffset.UTC)

                        val readableAtUtc = utcZoned.format(DateTimeFormatter.ISO_INSTANT)
                        capsuleViewModel.onReadableAtChange(readableAtUtc)

                        showTimePicker = false
                    },
                    onDismiss = { showTimePicker = false }
                )
            }

            OutlinedTextField(
                value = message,
                onValueChange = {
                    if (it.length <= 500) {
                        capsuleViewModel.onMessageChange(it)
                    }
                },
                label = { Text("Message") },
                minLines = 5,
                maxLines = 5,
                placeholder = { Text("Votre message ici (max 500 characters)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "${message.length} / 500",
                modifier = Modifier
                    .align(Alignment.End),
                style = MaterialTheme.typography.labelSmall
            )

            Button(
                onClick = {
                    capsuleViewModel.saveCapsule(
                        onComplete = {
                            onCapsuleCreated()
                            Toast.makeText(
                                context,
                                "Capsule enregistrée avec succès",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("timeCapsuleList")
                        },
                        onError = {
                            Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                },
                enabled = recipient.isNotEmpty() && message.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Enregistrer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun DatePickerModalPreview(
//    modifier: Modifier = Modifier
//) {
//    TimeCapsuleTheme {
//        DatePickerModal(onDateSelected = {}, onDismiss = {})
//    }
//}
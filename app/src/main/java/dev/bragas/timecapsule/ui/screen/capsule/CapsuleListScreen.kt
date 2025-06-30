package dev.bragas.timecapsule.ui.screen.capsule

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.bragas.timecapsule.ui.common.AlertDialogModal
import dev.bragas.timecapsule.ui.screen.auth.AuthViewModel
import dev.bragas.timecapsule.utils.Utils
import java.time.OffsetDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleListScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    capsuleViewModel: CapsuleViewModel = viewModel(),
    onCreateClick: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogTitle by remember { mutableStateOf("Opération non autorisée") }

    val tabs = listOf("Envoyés", "Réçus")
    val sent by capsuleViewModel.sent.collectAsState()
    val received by capsuleViewModel.received.collectAsState()
    val capsulesType = if (selectedTab == 0) sent else received
    val context = LocalContext.current
    val currentUser = authViewModel.currentUser.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capsule Temporelle") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signOut()
                        navController.navigate("signIn")
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreateClick() },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    ) {
                        Text(text = title)
                    }
                }
            }

            LazyColumn {
                items(capsulesType, key = { it.id }) { capsule ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart && !capsule.isRead && selectedTab == 0) {
                                capsuleViewModel.deleteCapsule(
                                    capsule.id,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Capsule supprimée!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = {
                                        Toast.makeText(
                                            context,
                                            "Erreur lors de la suppression de la capsule",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                                true
                            } else {
                                false
                            }
                        }
                    )

                    Box {
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .background(Color.Red)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = Color.White,
                                        modifier = Modifier.padding(end = 24.dp)
                                    )
                                }
                            },
                            content = {
                                CapsuleItem(
                                    capsule = capsule,
                                    onClick = {
                                        val readable = OffsetDateTime.parse(capsule.readableAt)
                                            .isBefore(OffsetDateTime.now())

                                        if (!readable) {
                                            dialogMessage =
                                                "Capsule verrouillée jusqu'à ${
                                                    Utils.utcToLocalDate(
                                                        capsule.readableAt
                                                    )
                                                }"
                                            dialogTitle = "Capsule verrouillée"
                                            showDialog = true
                                        } else if (capsule.recipientId != currentUser!!.uid) {
                                            dialogMessage =
                                                "Une fois qu'un message est envoyé, seul le destinataire peut l'ouvrir."
                                            dialogTitle = "Opération non autorisée"
                                            showDialog = true
                                        } else {
                                            navController.navigate("capsuleDetail/${capsule.id}")
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialogModal(
            dialogTitle = dialogTitle,
            dialogText = dialogMessage,
            icon = Icons.Outlined.Lock,
            onDismissRequest = { showDialog = false }
        )
    }
}

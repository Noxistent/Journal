package com.journal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.journal.JournalAPI.JournalAPI
import com.journal.StorageMMKV
import com.journal.UpdateTimeManager
import com.journal.ui.theme.JournalTheme
import com.journal.ui.theme.Red
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun LoginDialog(
    onDismiss:  () -> Unit
) {
    val login = rememberTextFieldState()
    val password = rememberTextFieldState()
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Dialog(onDismiss) {
        JournalTheme {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(16.dp)),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Journal", fontSize = 48.sp)
                        Spacer(Modifier.fillMaxHeight(0.3f))
                        TextField(
                            state = login,
                            label = { Text("UserName") }
                        )
                        Spacer(Modifier.fillMaxHeight(0.05f))
                        TextField(
                            state = password,
                            label = { Text("Password") }
                        )
                        Spacer(Modifier.fillMaxHeight(0.4f))
                        Text(error, color = Red)

                    }
                    Button(
                        onClick = {
                            scope.launch {
                                UpdateTimeManager().TestLogin(
                                    login,
                                    password,
                                    error = {newError ->
                                        error = newError
                                    },
                                    onDismiss
                                )
                            }

                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Logging")
                    }
                }
            }

        }
    }
}
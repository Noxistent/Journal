package com.journal.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import com.journal.JournalAPI.JournalAPI
import com.journal.JournalAPI.Timetable
import com.journal.R
import com.journal.StorageMMKV
import com.journal.UpdateTimeManager
import com.journal.ui.theme.JournalTheme
import com.journal.ui.theme.Red
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        StorageMMKV.init(this)

        setContent {
            var isFirstRun by remember { mutableStateOf(MMKV.defaultMMKV().decodeBool("isFirstRun", true)) }

            if (isFirstRun) {
                LoginDialog(
                    onDismiss = { isFirstRun = false }
                )

            } else {
                MainWindow()
            }

        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWindow(){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    JournalTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Journal",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    },
                    actions = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    UpdateTimeManager().updateTimetable()
                                    (context as? Activity)?.recreate()
                                    isLoading = false

                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.reload_1),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            },
            content = { it: PaddingValues ->
                val currentTimetable = StorageMMKV.getTimetable("CurrentTimetable")
                val nextTimetable = StorageMMKV.getTimetable("NextTimetable")

                if (currentTimetable != null && nextTimetable != null) {
                    val pagerState = rememberPagerState(pageCount = { currentTimetable.size + nextTimetable.size })

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 40.dp),
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) { page ->
                        Box(
                            Modifier
                                .graphicsLayer {
                                    val pageOffset = (
                                            (pagerState.currentPage - page) + pagerState
                                                .currentPageOffsetFraction
                                            ).absoluteValue

                                    val scale = lerp(
                                        start = 1f,
                                        stop = 0.85f,
                                        fraction = pageOffset.coerceIn(0f, 1f)
                                    )

                                    scaleX = scale
                                    scaleY = scale
                                }
                        ) {
                            if (page < currentTimetable.size) {
                                CardDay(LocalConfiguration.current, page, currentTimetable)
                            } else {
                                CardDay(LocalConfiguration.current, page - currentTimetable.size, nextTimetable)
                            }

                        }
                    }

                    // Переход на твой день при запуске
                    coroutineScope.launch {
                        val nowData = LocalDate.now()
                        var num = 0
                        for (currentDayOnTimetable in currentTimetable) {
                            if (nowData == currentDayOnTimetable.day) {
                                pagerState.scrollToPage(num)
                            }
                            num += 1
                        }
                    }
                }
            }
        )
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CardDay(configuration: Configuration, numCard: Int, timetable:  MutableList<Timetable>) {
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Column(
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {

            val dayOfWeek = timetable[numCard].day.dayOfWeek.getDisplayName(
                TextStyle.FULL,
                Locale.Builder()
                    .setLanguage("ru")
                    .setRegion("RU")
                    .build()
            )

            Text(text = dayOfWeek, fontSize = 40.sp)
            Text(timetable[numCard].day.toString())
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(timetable[numCard].listLessons) { lesson ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(screenWidth * 0.8f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = lesson.subjectName,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.fillMaxHeight(0.05f))

                        val time =
                            lesson.startedAt.toString() + " - " + lesson.finishedAt.toString()
                        Text(time)

                        Spacer(Modifier.fillMaxHeight(0.05f))

                        Text(lesson.roomName)

                        Spacer(Modifier.fillMaxHeight(0.05f))

                        Text(lesson.teacherName)
                    }
                }
            }
        }

    }
}

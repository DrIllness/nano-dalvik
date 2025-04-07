package com.nanodalvik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nanodalvik.data.OpCodeNames
import com.nanodalvik.ui.AppColors
import com.nanodalvik.ui.NortonButton
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    MiniDalvikUI()
}

@Preview(showBackground = true)
@Composable
fun MiniDalvikUI() {
    val app = (LocalContext.current.applicationContext as NanoDalvikApp)

    val bytecode = remember { mutableStateOf(TextFieldValue("")) }
    val consoleOutput = app.observeOutput().map { logList ->
        var str = ""
        for (entry in logList) {
            str += "${entry.str}\n"
        }
        str
    }.collectAsState(initial = "")
    val stackState = app.observeStackState().collectAsState(emptyList())
    val scope = rememberCoroutineScope()

    Column(
            modifier = Modifier
                .border(color = AppColors.border, width = 2.dp)
                .fillMaxSize()
                .background(AppColors.bg)
    ) {
        Column {
            Text(
                    stringResource(id = R.string.bytecode_editor), color = AppColors.title,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Row {
                Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                ) {
                    TextField(
                            value = bytecode.value,
                            onValueChange = { bytecode.value = it },
                            modifier = Modifier
                                .fillMaxHeight(0.3f)
                                .fillMaxWidth(0.7f)
                                .border(2.dp, AppColors.border),
                            textStyle = TextStyle(
                                    fontFamily = FontFamily.Monospace, color = AppColors.border
                            )
                    )
                }
                // side buttons
                Column(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    for (code in OpCodeNames.entries) {
                        NortonButton(
                                text = code.name,
                                onClick = {
                                    val positionOfCursor = bytecode.value.selection.end
                                    val sizeOfAddedStr = "${code.name} ".length
                                    val newText = bytecode.value.text.substring(0, positionOfCursor) +
                                            "${code.name} " +
                                            bytecode.value.text.substring(positionOfCursor, bytecode.value.selection.end)
                                    bytecode.value = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(positionOfCursor + sizeOfAddedStr)
                                    )},
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth()
                        )
                    }
                }
            }

            Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            ) {
                NortonButton(
                        text = stringResource(id = R.string.run),
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 2.dp),
                        onClick = {
                            // todo move it out of here
                            scope.launch {
                                app.runProgram(bytecode.value.text)
                            }
                        })
                NortonButton(
                        text = stringResource(id = R.string.step),
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(start = 2.dp),
                        onClick = {
                            scope.launch {
                                app.nextStep(bytecode.value.text)
                            }
                        })
            }
        }

        Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
        ) {
            Text(
                    stringResource(id = R.string.console_output), color = AppColors.title,
                    fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 2.dp)
            )
            Box(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .fillMaxWidth()
                        .border(2.dp, Color.Cyan)
            ) {
                Text(
                        consoleOutput.value, color = Color.Green,
                        modifier = Modifier.padding(4.dp)
                )
            }

            Text(
                    stringResource(id = R.string.stack_visualizer), color = AppColors.title,
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontWeight = FontWeight.ExtraBold
            )
            LazyColumn(
                    modifier = Modifier
                        .border(2.dp, Color.Cyan)
                        .padding(horizontal = 4.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
            ) {
                items(stackState.value.mapIndexed { i, v -> "[$i] $v" }) { item ->
                    Text(
                            item, color = Color.Cyan, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

package com.jr.sudokungfu.ui.game

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jr.sudokungfu.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuBoard(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val grid = state.gameBoard
    val focusManager = LocalFocusManager.current
//    val isGameSolved = remember { state.isGameSolved }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        if (state.isGameSolved) {
            Toast.makeText(LocalContext.current, "Solved..!", Toast.LENGTH_SHORT).show()
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier
                .fillMaxWidth(1f)
                .wrapContentHeight(align = Alignment.Top)
                .background(Color.White)
                .border(0.5.dp, Color.Black),
            content = {
                items(81) {
                    val indexRow = (it / 9)
                    val indexColumn = ((it + 1) % 9)
                    val gridItem = grid[indexRow][indexColumn]
                    val gridItemValue = gridItem.cellValue

                    var text by mutableStateOf(
                        TextFieldValue(
                            if (gridItemValue == 0) "" else gridItemValue.toString()
                        )
                    )

                    val isEditable = gridItem.isEditable
                    val color = if (isEditable) Color.White else Color.LightGray

                    BasicTextField(
                        value = text,
                        enabled = isEditable,
                        onValueChange = { newText ->
                            //max length 1
                            if (newText.text.isBlank()) {
                                text = newText
                            } else if (newText.text.length == 1 && newText.text in "0".."9") {
                                text = newText
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .widthIn(56.dp)
                            .fillMaxHeight(1f)
                            .aspectRatio(1f)
                            .padding(0.dp)
                            .border(0.5.dp, Color.Black)
                            .background(color)
                            .wrapContentHeight(),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            })
        Spacer(modifier = Modifier.height(16.dp))

        val scope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val levelText = state.getGameLevel()

        GameOptions(
            levelText = levelText,
            onLevelClick = {
                scope.launch {
                    viewModel.makeGameIdle()
                    sheetState.show()
                }.invokeOnCompletion {
                    if (sheetState.isVisible) {
                        showBottomSheet = true
                    }
                }
            },
            onSolveClick = viewModel::solve,
            onResetClick = viewModel::reset
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 48.dp)
                ) {
                    LevelOption(text = "Easy") {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                viewModel.changeLevel(Level.Easy)
                                viewModel.reset()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LevelOption(text = "Medium") {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                viewModel.changeLevel(Level.Medium)
                                viewModel.reset()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LevelOption(text = "Hard") {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                viewModel.changeLevel(Level.Hard)
                                viewModel.reset()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Dismiss",
                            style = TextStyle(color = Color.Red.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelOption(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        onClick = onClick
    ) {
        Text(text)
    }
}

@Composable
fun GameOptions(
    levelText: String,
    onLevelClick: () -> Unit,
    onSolveClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier,
            onClick = onLevelClick
        ) {
            Text(text = "Level : $levelText")
        }
        Spacer(modifier = Modifier.width(24.dp))
        Button(
            modifier = Modifier,
            onClick = onSolveClick
        ) {
            Text(text = "Solve")
        }
        Spacer(modifier = Modifier.width(24.dp))
        Button(
            modifier = Modifier,
            onClick = onResetClick
        ) {
            Text(text = "Reset")
        }
    }
}
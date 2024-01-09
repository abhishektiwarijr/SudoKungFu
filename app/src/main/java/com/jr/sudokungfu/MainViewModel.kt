package com.jr.sudokungfu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jr.sudokungfu.ui.game.Cell
import com.jr.sudokungfu.ui.game.GameUiState
import com.jr.sudokungfu.ui.game.Level
import com.jr.sudokungfu.ui.game.SudokuMaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    private val sudokuMaster by lazy {
        SudokuMaster(Level.Easy)
    }

    private var _uiState = MutableStateFlow(GameUiState(gameBoard = sudokuMaster.getBoard()))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

//    private var _board: MutableStateFlow<Array<Array<Cell>>> =
//        MutableStateFlow(sudokuMaster.getBoard())
//    val board: StateFlow<Array<Array<Cell>>>
//        get() = _board.asStateFlow()

//    private fun fillBoard(): Array<IntArray> {
//        return arrayOf(
//            intArrayOf(7, 0, 2, 0, 5, 0, 6, 0, 0),
//            intArrayOf(0, 0, 0, 0, 0, 3, 0, 0, 0),
//            intArrayOf(1, 0, 0, 0, 0, 9, 5, 0, 0),
//            intArrayOf(8, 0, 0, 0, 0, 0, 0, 9, 0),
//            intArrayOf(0, 4, 3, 0, 0, 0, 7, 5, 0),
//            intArrayOf(0, 9, 0, 0, 0, 0, 0, 0, 8),
//            intArrayOf(0, 0, 9, 7, 0, 0, 0, 0, 5),
//            intArrayOf(0, 0, 0, 2, 0, 0, 0, 0, 0),
//            intArrayOf(0, 0, 7, 0, 4, 0, 2, 0, 3)
//        )
//    }

//    private fun isNumberInRow(board: Array<IntArray>, number: Int, row: Int): Boolean {
//        for (i in 0 until GRID_SIZE) {
//            if (board[row][i] == number) {
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun isNumberInColumn(board: Array<IntArray>, number: Int, column: Int): Boolean {
//        for (i in 0 until GRID_SIZE) {
//            if (board[i][column] == number) {
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun isNumberInBox(board: Array<IntArray>, number: Int, row: Int, column: Int): Boolean {
//        val localBoxRow = row - row % 3
//        val localBoxColumn = column - column % 3
//        for (i in localBoxRow until localBoxRow + 3) {
//            for (j in localBoxColumn until localBoxColumn + 3) {
//                if (board[i][j] == number) {
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    private fun isValidPlacement(
//        board: Array<IntArray>,
//        number: Int,
//        row: Int,
//        column: Int
//    ): Boolean {
//        return (!isNumberInRow(board, number, row)
//                && !isNumberInColumn(board, number, column)
//                && !isNumberInBox(board, number, row, column))
//    }

    fun changeLevel(level: Level) {
        viewModelScope.launch {
            sudokuMaster.level = level
            _uiState.update { gameUiState ->
                gameUiState.copy(
                    level = level,
                    isGameSolved = false
                )
            }
        }
    }

    fun reset() {
        viewModelScope.launch {
            val newBoard = getBoardAfterReset()
            _uiState.update { gameUiState ->
                gameUiState.copy(
                    gameBoard = newBoard,
                    isGameSolved = false,
                )
            }
        }
    }

    private suspend fun getBoardAfterReset() = withContext(Dispatchers.Default) {
        return@withContext async { sudokuMaster.getNewBoard() }.await()
    }


    fun solve() {
        if (sudokuMaster.solveBoard(_uiState.value.gameBoard)) {
            _uiState.update { currentState ->
                currentState.copy(
                    gameBoard = _uiState.value.gameBoard,
                    isGameSolved = true,
                )
            }
        }
    }

    fun makeGameIdle() {
        _uiState.update { gameUiState ->
            gameUiState.copy(
                isGameSolved = false,
            )
        }
    }
}
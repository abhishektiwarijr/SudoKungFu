package com.jr.sudokungfu.ui.game

data class GameUiState(
    val gameBoard: Array<Array<Cell>> = arrayOf<Array<Cell>>(),
    val isGameSolved: Boolean = false,
    val level: Level = Level.Easy
) {

    fun getGameLevel() = when(level) {
        Level.Easy -> LEVEL_EASY
        Level.Medium -> LEVEL_MEDIUM
        Level.Hard -> LEVEL_HARD
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as GameUiState
//
//        if (!gameBoard.contentDeepEquals(other.gameBoard)) return false
//        if (isGameSolved != other.isGameSolved) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = gameBoard.contentDeepHashCode()
//        result = 31 * result + isGameSolved.hashCode()
//        return result
//    }
}
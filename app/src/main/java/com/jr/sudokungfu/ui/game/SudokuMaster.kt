package com.jr.sudokungfu.ui.game

import kotlin.random.Random

class SudokuMaster(var level: Level) {
    private val sampleBoard = arrayOf(
        intArrayOf(7, 0, 2, 0, 5, 0, 6, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 3, 0, 0, 0),
        intArrayOf(1, 0, 0, 0, 0, 9, 5, 0, 0),
        intArrayOf(8, 0, 0, 0, 0, 0, 0, 9, 0),
        intArrayOf(0, 4, 3, 0, 0, 0, 7, 5, 0),
        intArrayOf(0, 9, 0, 0, 0, 0, 0, 0, 8),
        intArrayOf(0, 0, 9, 7, 0, 0, 0, 0, 5),
        intArrayOf(0, 0, 0, 2, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 7, 0, 4, 0, 2, 0, 3)
    )

    private val board = Array<Array<Cell>>(GRID_SIZE) {
        Array<Cell>(GRID_SIZE) {
            Cell(it + 1, 0)
        }
    }

    init {
        fillBoardFirst()
    }

    private fun fillBoardFirst() {
        for (row in 0 until GRID_SIZE) {
            for (column in 0 until GRID_SIZE) {
                val cellId = row * 9 + column % 9 + 1
                val cellValue = sampleBoard[row][column]
                board[row][column] = Cell(
                    cellId = cellId,
                    cellValue = cellValue,
                    isEditable = cellValue == 0
                )
            }
        }
    }


    private fun fillBoard(board: Array<Array<Cell>>) {
        fillDiagonalBoxes(board)
        fillRemaining(board, 0, GRID_SIZE_SQUARE_ROOT)
        removeDigits(board)
    }

    private fun Array<Array<Cell>>.copy() = Array(size) { get(it).clone() }

    fun getBoard() = board.copy()

    suspend fun getNewBoard() = board
        .copy()
        .also {
            resetBoard(it)
            fillBoard(it)
        }

    private fun resetBoard(board: Array<Array<Cell>>) {
        for (row in 0 until GRID_SIZE) {
            for (column in 0 until GRID_SIZE) {
                board[row][column].apply {
                    cellValue = 0
                    isEditable = true
                }
            }
        }
    }

    private fun removeDigits(board: Array<Array<Cell>>) {
        var digitsToRemove = GRID_SIZE * GRID_SIZE - level.noOfDigitsPrefilled

        while (digitsToRemove > 0) {
            val randomRow = generateRandomDigit(MIN_DIGIT_INDEX, MAX_DIGIT_INDEX)
            val randomColumn = generateRandomDigit(MIN_DIGIT_INDEX, MAX_DIGIT_INDEX)

            if (board[randomRow][randomColumn].cellValue != 0) {
                board[randomRow][randomColumn].apply {
                    cellValue = 0
                    isEditable = true
                }
                digitsToRemove--
            }
        }
    }

    private fun canBeSolved(board: Array<IntArray>): Boolean {
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                if (board[i][j] == 0) {
                    val availableDigits = getAvailableDigits(board, i, j)
                    for (k in availableDigits) {
                        board[i][j] = k
                        if (canBeSolved(board)) {
                            return true
                        }
                        board[i][j] = 0
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun getAvailableDigits(board: Array<IntArray>, row: Int, column: Int): Iterable<Int> {
        val digitsRange = MIN_DIGIT_VALUE..MAX_DIGIT_VALUE
        val availableDigits = mutableSetOf<Int>()
        availableDigits.addAll(digitsRange)

        truncateByDigitsAlreadyUsedInRow(board, availableDigits, row)
        if (availableDigits.size > 1) {
            truncateByDigitsAlreadyUsedInColumn(board, availableDigits, column)
        }
        if (availableDigits.size > 1) {
            truncateByDigitsAlreadyUsedInBox(board, availableDigits, row, column)
        }

        return availableDigits.asIterable()
    }

    private fun truncateByDigitsAlreadyUsedInRow(
        board: Array<IntArray>,
        availableDigits: MutableSet<Int>,
        row: Int
    ) {
        for (i in MIN_DIGIT_INDEX..MAX_DIGIT_INDEX) {
            if (board[row][i] != 0) {
                availableDigits.remove(board[row][i])
            }
        }
    }

    private fun truncateByDigitsAlreadyUsedInColumn(
        board: Array<IntArray>,
        availableDigits: MutableSet<Int>,
        column: Int
    ) {
        for (i in MIN_DIGIT_INDEX..MAX_DIGIT_INDEX) {
            if (board[i][column] != 0) {
                availableDigits.remove(board[i][column])
            }
        }
    }

    private fun truncateByDigitsAlreadyUsedInBox(
        board: Array<IntArray>,
        availableDigits: MutableSet<Int>,
        row: Int,
        column: Int
    ) {
        val rowStart = row - row % 3
        val rowEnd = rowStart + BOX_SIZE - 1

        val columnStart = column - column % 3
        val columnEnd = columnStart + BOX_SIZE - 1

        for (i in rowStart until rowEnd) {
            for (j in columnStart until columnEnd) {
                if (board[i][j] != 0) {
                    availableDigits.remove(board[i][j])
                }
            }
        }
    }

    private fun fillRemaining(board: Array<Array<Cell>>, gridBegin: Int, gridSize: Int): Boolean {
        var i = gridBegin
        var j = gridSize

        if (j >= GRID_SIZE && i < GRID_SIZE - 1) {
            i += 1
            j = 0
        }
        if (i >= GRID_SIZE && j >= GRID_SIZE) {
            return true
        }

        if (i < GRID_SIZE_SQUARE_ROOT) {
            if (j < GRID_SIZE_SQUARE_ROOT) {
                j = GRID_SIZE_SQUARE_ROOT
            }
        } else if (i < GRID_SIZE - GRID_SIZE_SQUARE_ROOT) {
            if (j == (i / GRID_SIZE_SQUARE_ROOT) * GRID_SIZE_SQUARE_ROOT) {
                j += GRID_SIZE_SQUARE_ROOT
            }
        } else {
            if (j == GRID_SIZE - GRID_SIZE_SQUARE_ROOT) {
                i += 1
                j = 0
                if (i >= GRID_SIZE) {
                    return true
                }
            }
        }

        for (digit in 1..MAX_DIGIT_VALUE) {
            if (isValidPlacement(board, digit, i, j)) {
                board[i][j].apply {
                    cellValue = digit
                    isEditable = false
                }
                if (fillRemaining(board, i, j + 1)) {
                    return true
                }
                board[i][j].apply {
                    cellValue = 0
                    isEditable = true
                }
            }
        }

        return false
    }

    private fun isValidPlacement(
        board: Array<Array<Cell>>,
        number: Int,
        row: Int,
        column: Int,
    ): Boolean {
        return (!isNumberInRow(board, number, row)
                && !isNumberInColumn(board, number, column)
                && !isNumberInBox(board, number, row, column))
    }

    private fun isNumberInBox(board: Array<Array<Cell>>, number: Int, row: Int, column: Int): Boolean {
        val localBoxRow = row - row % 3
        val localBoxColumn = column - column % 3
        for (i in localBoxRow until localBoxRow + 3) {
            for (j in localBoxColumn until localBoxColumn + 3) {
                if (board[i][j].cellValue == number) {
                    return true
                }
            }
        }
        return false
    }

    private fun isNumberInColumn(board: Array<Array<Cell>>, number: Int, column: Int): Boolean {
        for (i in 0 until GRID_SIZE) {
            if (board[i][column].cellValue == number) {
                return true
            }
        }
        return false
    }

    private fun isNumberInRow(board: Array<Array<Cell>>, number: Int, row: Int): Boolean {
        for (i in 0 until GRID_SIZE) {
            if (board[row][i].cellValue == number) {
                return true
            }
        }
        return false
    }

    private fun fillDiagonalBoxes(board: Array<Array<Cell>>) {
        for (i in 0 until GRID_SIZE step GRID_SIZE_SQUARE_ROOT) {
            fillBox(board, i, i)
        }
    }

    private fun fillBox(board: Array<Array<Cell>>, row: Int, column: Int) {
        var generatedDigit: Int
        for (i in 0 until GRID_SIZE_SQUARE_ROOT) {
            for (j in 0 until GRID_SIZE_SQUARE_ROOT) {
                do {
                    generatedDigit = generateRandomDigit(MIN_DIGIT_VALUE, MAX_DIGIT_VALUE)
                } while (isAlreadyInBoard(board, row, column, generatedDigit))

                board[row + i][column + j].apply {
                    cellValue = generatedDigit
                    isEditable = false
                }
            }
        }
    }

    private fun isAlreadyInBoard(
        board: Array<Array<Cell>>,
        rowStart: Int,
        columnStart: Int,
        digit: Int
    ): Boolean {
        for (i in 0 until GRID_SIZE_SQUARE_ROOT) {
            for (j in 0 until GRID_SIZE_SQUARE_ROOT) {
                if (board[rowStart + i][columnStart + j].cellValue == digit) {
                    return true
                }
            }
        }
        return false
    }

    private fun generateRandomDigit(min: Int, max: Int) = Random.nextInt(min, max + 1)


    fun solveBoard(board: Array<Array<Cell>>): Boolean {
        for (row in 0 until GRID_SIZE) {
            for (column in 0 until GRID_SIZE) {
                if (board[row][column].cellValue == 0) {
                    for (numberToTry in MIN_DIGIT_VALUE..MAX_DIGIT_VALUE) {
                        if (isValidPlacement(board, numberToTry, row, column)) {
                            board[row][column].cellValue = numberToTry
                            if (solveBoard(board)) {
                                return true
                            } else {
                                board[row][column].cellValue = 0
                            }
                        }
                    }
                    return false
                }
            }
        }
        return true
    }
}
package com.jr.sudokungfu.ui.game

sealed class Level(val noOfDigitsPrefilled : Int) {
    object Easy : Level(25)
    object Medium : Level(20)
    object Hard : Level(17)
}
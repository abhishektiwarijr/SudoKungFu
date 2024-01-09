package com.jr.sudokungfu.ui.game

data class Cell(
    val cellId : Int,
    var cellValue : Int,
    var isEditable : Boolean = false
)

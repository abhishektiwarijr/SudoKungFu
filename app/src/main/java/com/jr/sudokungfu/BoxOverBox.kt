package com.jr.sudokungfu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension


@Composable
fun BoxOverBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .background(Color.Red)
        )
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .zIndex(2f)
                .graphicsLayer {
                    translationX = -50f
                    translationY = 50f
                }
                .background(Color.Blue)
        )
    }
}

@Composable
fun BoxOverBoxWithConstraints() {
    BoxWithConstraints(
        modifier = Modifier
            .background(color = Color.Blue)
            .padding(20.dp)
    ) {
        val boxWidth = this.maxWidth
        Box(
            modifier = Modifier
                .width(boxWidth - 10.dp)
                .background(Color.Red)
        ) {
            Text(text = "hello android")
        }
        Column {
            Spacer(
                modifier = Modifier
                    .height(10.dp)
                    .width(10.dp)
            )
            Row {
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .width(boxWidth)
                        .zIndex(2f)
                        .background(Color.Yellow)
                ) {
                    Text("aa", modifier = Modifier.background(color = Color.Green))
                }
            }
        }
    }
}

@Preview
@Composable
fun BoxOverBoxConstraintLayout() {
    ConstraintLayout {
        val (title, description) = createRefs()
        Box(
            modifier = Modifier
                .padding(start = 28.dp)
                .background(color = Color.Red)
                .padding(horizontal = 16.dp)
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(text = "hello world")
        }

        Box(
            modifier = Modifier
                .padding(end = 4.dp)
                .background(Color.Magenta)
                .padding(bottom = 5.dp, start = 8.dp, end = 16.dp, top = 4.dp)
                .constrainAs(description) {
                    top.linkTo(title.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text(text = "skizo-ozᴉʞs rules")
        }
    }
}
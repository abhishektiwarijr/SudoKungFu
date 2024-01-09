package com.jr.sudokungfu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSliderComponent(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    val scope = rememberCoroutineScope()
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        ) {
            HorizontalPager(
                state = pagerState,
            ) { index ->
//            val painter = rememberAsyncImagePainter(
//                ImageRequest.Builder(LocalContext.current)
//                    .data(data = images[index])
//                    .apply(block = fun ImageRequest.Builder.() {
//                        size(Size.ORIGINAL)
//                    }).build()
//            )
                val painter = rememberAsyncImagePainter(
                    model = images[index],
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painter,
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (painter.state is AsyncImagePainter.State.Loading) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }

        //bottom row with buttons(Left Arrow And Right Arrow) and dots indicators
        Row(
            modifier = Modifier
//                .layout { measurable, constraints ->
//                    val horizontalExtraPadding = 8.dp.roundToPx()
//
//                    // Measure the composable adding the side padding*2 (left+right)
//                    val placeable =
//                        measurable.measure(
//                            constraints.offset(
//                                horizontal = horizontalExtraPadding * 2,
//                            )
//                        )
//                    //increase the width adding the side padding*2
//                    layout(
//                        placeable.width + horizontalExtraPadding * 2,
//                        placeable.height
//                    ) {
//                        // Where the composable gets placed
//                        placeable.place(+horizontalExtraPadding, 0)
//                    }
//                }
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.Yellow)
        ) {
            ArrowIcon(
                iconType = ArrowIcon.LEFT
            ) {
                scope.launch {
                    pagerState.animateScrollToPage(
                        pagerState.currentPage - 1
                    )
                }
            }

            DotsIndicator(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .weight(1f)
                    .align(Alignment.Bottom),
                totalDots = images.size,
                selectedIndex = if (isDragged) pagerState.currentPage else pagerState.targetPage,
                dotSize = 8.dp
            )

            ArrowIcon(
                iconType = ArrowIcon.RIGHT
            ) {
                scope.launch {
                    pagerState.animateScrollToPage(
                        pagerState.currentPage + 1
                    )
                }
            }
        }
    }
}
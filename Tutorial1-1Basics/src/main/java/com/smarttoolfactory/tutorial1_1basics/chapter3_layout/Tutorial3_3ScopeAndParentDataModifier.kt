package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial3_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Scope and ParentDataModifier")
        TutorialText(
            text = "1-) Using a scope for a Composable it's possible to add Modifier " +
                    "only available in that scope. Modifier.horizontalAlign " +
                    "is only available in CustomColumnScope."
        )

        TutorialText2(text = "Custom Column with Scope")

        CustomColumnWithScope(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.LightGray)
        ) {

            Text(
                "Align Start",
                modifier = Modifier
                    .background(Color(0xffF44336))
                    .horizontalAlign(HorizontalAlignment.Start),
                color = Color.White
            )
            Text(
                "Align Center",
                modifier = Modifier
                    .background(Color(0xff9C27B0))
                    .horizontalAlign(HorizontalAlignment.Center),
                color = Color.White
            )
            Text(
                "Align End",
                modifier = Modifier
                    .background(Color(0xff2196F3))
                    .horizontalAlign(HorizontalAlignment.End),
                color = Color.White
            )
            Text(
                "Align Start",
                modifier = Modifier
                    .background(Color(0xff8BC34A))
                    .horizontalAlign(HorizontalAlignment.Start),
                color = Color.White
            )
        }
    }
}

/*
1- Create a enum for setting horizontal alignment options
 */
enum class HorizontalAlignment {
    Start, Center, End
}


/*
3- Create a interface for Scope that has an extension function that returns a class
that implements ParentDataModifier interface
 */
interface CustomColumnScope {

    @Stable
    fun Modifier.horizontalAlign(align: HorizontalAlignment) = this.then(
        CustomColumnData(align)
    )

    companion object : CustomColumnScope
}


/*
2- Create a class that implements ParentDataModifier and implement functions
 */
private class CustomColumnData(
    val alignment: HorizontalAlignment
) : ParentDataModifier {

    override fun Density.modifyParentData(parentData: Any?) = this@CustomColumnData


    override fun equals(other: Any?): Boolean {

        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as CustomColumnData

        if (alignment != other.alignment) return false

        return true
    }

    override fun hashCode(): Int {
        return alignment.hashCode()
    }

    override fun toString(): String =
        "CustomColumnData(alignment=$alignment)"
}


/*
4- Create extension functions to set this ParentDataModifier in custom Layout using measurable
 */

private val Measurable.childData: CustomColumnData?
    get() = parentData as? CustomColumnData

private val Measurable.alignment: HorizontalAlignment
    get() = childData?.alignment ?: HorizontalAlignment.Start


@Composable
fun CustomColumnWithScope(
    modifier: Modifier = Modifier,
    content: @Composable CustomColumnScope.() -> Unit
) {

    Layout(
        modifier = modifier,
        content = { CustomColumnScope.content() },
    ) { measurables: List<Measurable>, constraints: Constraints ->

        // We need to set minWidth to zero to wrap only placeable width
        val looseConstraints = constraints.copy(
            minWidth = 0,
            minHeight = constraints.minHeight
        )

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(looseConstraints)
        }

        // 🔥 We will use this alignment to set position of our composables
        val measurableAlignment: List<HorizontalAlignment> = measurables.map { measurable ->
            measurable.alignment
        }

        // Track the y co-ord we have placed children up to
        var yPosition = 0

        val totalHeight: Int = placeables.map {
            it.height
        }.sum()

        val maxWidth = constraints.maxWidth

        println(
            "🤯 Constraints minWidth: ${constraints.minWidth}, " +
                    "minHeight: ${constraints.minHeight}, " +
                    "maxWidth: ${constraints.maxWidth}, " +
                    "maxHeight: ${constraints.maxHeight}"
        )

        // Set the size of the layout as big as it can
        layout(maxWidth, totalHeight) {
            // Place children in the parent layout
            placeables.forEachIndexed { index, placeable ->

                val x = when (measurableAlignment[index]) {
                    HorizontalAlignment.Start -> 0
                    HorizontalAlignment.Center -> (maxWidth - placeable.measuredWidth) / 2
                    HorizontalAlignment.End -> maxWidth - placeable.measuredWidth
                }

                // Position item on the screen
                placeable.placeRelative(x = x, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}


//enum class ChipAlignment {
//    Top,
//    Center,
//    Bottom,
//}
//
//interface StaggeredGridScope {
//
//    fun Modifier.align(alignment: ChipAlignment) = this.then(
//        StaggerGridData(
//            alignment = alignment,
//        )
//    )
//
//    companion object : StaggeredGridScope
//}
//
//
//private class StaggerGridData(
//    val alignment: ChipAlignment
//) : ParentDataModifier {
//
//    override fun Density.modifyParentData(parentData: Any?) = this@StaggerGridData
//
//    override fun equals(other: Any?): Boolean {
//
//        if (this === other) return true
//
//        if (javaClass != other?.javaClass) return false
//
//        other as StaggerGridData
//
//        if (alignment != other.alignment) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return alignment.hashCode()
//    }
//
//    override fun toString(): String =
//        "StaggerGridData(alignment=$alignment)"
//}
//
//private val Measurable.childData: StaggerGridData? get() = parentData as? StaggerGridData
//private val Measurable.align: ChipAlignment get() = childData?.alignment ?: ChipAlignment.Center
//
//@Composable
//fun ChipStaggeredGrid(
//    modifier: Modifier = Modifier,
//    content: @Composable StaggeredGridScope.() -> Unit
//) {
//
//    Layout(
//        content = { StaggeredGridScope.content() },
//        modifier = modifier
//    ) { measurables: List<Measurable>, constraints: Constraints ->
//
//        val constraintMaxWidth = constraints.maxWidth
//        val constraintMaxHeight = constraints.maxHeight
//
//        var maxRowWidth = 0
//
//        var currentWidthOfRow = 0
//        var totalHeightOfRows = 0
//
//        var xPos: Int
//        var yPos: Int
//
//        val placeableMap = linkedMapOf<Int, Point>()
//        val rowHeights = mutableListOf<Int>()
//
//        var maxPlaceableHeight = 0
//        var lastRowHeight = 0
//
//        println("😈 MyStaggeredGrid() constraintMaxWidth: $constraintMaxWidth, constraintMaxHeight: $constraintMaxHeight")
//
//        val placeables: List<Placeable> = measurables.mapIndexed { index, measurable ->
//            // Measure each child
//            val placeable = measurable.measure(constraints)
//            val placeableWidth = placeable.width
//            val placeableHeight = placeable.height
//
//            val alignment: ChipAlignment = measurable.align
//
//            val isSameRow = (currentWidthOfRow + placeableWidth <= constraintMaxWidth)
//
//            if (isSameRow) {
//
//                xPos = currentWidthOfRow
//                yPos = totalHeightOfRows
//
//                // Current width or row is now existing length and new item's length
//                currentWidthOfRow += placeableWidth
//
//                // Get the maximum item height in each row
//                maxPlaceableHeight = maxPlaceableHeight.coerceAtLeast(placeableHeight)
//
//                // After adding each item check if it's the longest row
//                maxRowWidth = maxRowWidth.coerceAtLeast(currentWidthOfRow)
//
//                lastRowHeight = maxPlaceableHeight
//
//                println(
//                            "currentWidthOfRow: $currentWidthOfRow, " +
//                            "placeableHeight: $placeableHeight, " +
//                            "maxPlaceableHeight: $maxPlaceableHeight, alignment: $alignment"
//                )
//
//            } else {
//
//                currentWidthOfRow = placeableWidth
//                maxPlaceableHeight = maxPlaceableHeight.coerceAtLeast(placeableHeight)
//
//                totalHeightOfRows += maxPlaceableHeight
//
//                xPos = 0
//                yPos = totalHeightOfRows
//
//                rowHeights.add(maxPlaceableHeight)
//
//                lastRowHeight = maxPlaceableHeight
//                maxPlaceableHeight = placeableHeight
//
//                println(
//                            "currentWidthOfRow: $currentWidthOfRow, " +
//                            "totalHeightOfRows: $totalHeightOfRows, " +
//                            "placeableHeight: $placeableHeight, " +
//                            "maxPlaceableHeight: $maxPlaceableHeight, alignment: $alignment"
//                )
//            }
//
//            placeableMap[index] = Point(xPos, yPos)
//            placeable
//        }
//
//
//        val finalHeight = (rowHeights.sumOf { it } + lastRowHeight)
//            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))
//
//
//        println("RowHeights: $rowHeights, finalHeight: $finalHeight")
//
//        // Set the size of the layout as big as it can
//        layout(maxRowWidth, finalHeight) {
//            // Place children in the parent layout
//            placeables.forEachIndexed { index, placeable ->
//                // Position item on the screen
//
//                val point = placeableMap[index]
//                point?.let {
//                    placeable.placeRelative(x = point.x, y = point.y)
//                }
//            }
//        }
//    }
//}
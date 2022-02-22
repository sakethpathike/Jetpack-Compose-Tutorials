package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.*

@Composable
fun Tutorial5_4Screen4() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        ConsumeDownAndUpEventsExample()
        ConsumeDownAndMoveEventsExample()
        ConsumeDragEventsExample()
    }

}

@Composable
private fun ConsumeDownAndUpEventsExample() {

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }
    // This text is drawn to Text composable
    var gestureText by remember { mutableStateOf("") }

    val pointerModifier = gestureModifier
        .background(gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown(requireUnconsumed = false)

                    var eventChanges = ""
                    gestureColor = Blue400

                    // Consuming down causes changeToDown to return false
                    // And other events like scroll to not interfere with this event
                    down.consumeDownChange()

                    eventChanges =
                        "🎃DOWN\n" +
                                "changedToDown: ${down.changedToDown()}, " +
                                "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                                "pressed: ${down.pressed}\n" +
                                "changedUp: ${down.changedToUp()}\n" +
                                "positionChanged: ${down.positionChanged()}\n" +
                                "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                                "positionChangeConsumed: ${down.positionChangeConsumed()}\n"

                    gestureText = eventChanges

                    // 🔥 Wait for Up Event, this is called if only one pointer exits
                    // when it's up or moved out of Composable bounds
                    // When multiple pointers touch Composable it requires only one to be
                    // out of Composable bounds
                    val upOrCancel: PointerInputChange? = waitForUpOrCancellation()

                    if (upOrCancel != null) {

                        // Consume the up or down change of this PointerInputChange
                        // if there is an up or down change to consume.
                        // However verticalScroll() causes any vertical scroll to return NULL
//                        up.consumeDownChange()

                        eventChanges +=
                            "🍒UP\n" +
                                    "changedToDown: ${upOrCancel.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${upOrCancel.changedToDownIgnoreConsumed()}\n" +
                                    "pressed: ${upOrCancel.pressed}\n" +
                                    "changedUp: ${upOrCancel.changedToUp()}\n" +
                                    "changedToUpIgnoreConsumed: ${upOrCancel.changedToUpIgnoreConsumed()}\n" +
                                    "anyChangeConsumed: ${upOrCancel.anyChangeConsumed()}\n" +
                                    "positionChangeConsumed: ${upOrCancel.positionChangeConsumed()}\n"
                        gestureColor = Green400
                    } else {
                        eventChanges += "UP CANCEL"
                        gestureColor = Red400
                    }

                    gestureText = eventChanges
                }
            }
        }

    Box(modifier = pointerModifier, contentAlignment = Alignment.Center) {
        Text(
            text = "Touch Here\nto display down or up consume events",
            textAlign = TextAlign.Center
        )
    }

    GestureDisplayBox(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        gestureText = gestureText
    )
}

@Composable
private fun ConsumeDownAndMoveEventsExample() {

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }
    // This text is drawn to Text composable
    var gestureText by remember { mutableStateOf("") }

    val pointerModifier = gestureModifier
        .background(gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown()

                    var eventChanges = ""
                    gestureColor = Blue400

                    down.consumeDownChange()

                    do {

                        // This PointerEvent contains details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()

                        eventChanges =
                            "🎃DOWN\n" +
                                    "changedToDown: ${down.changedToDown()}, " +
                                    "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                                    "pressed: ${down.pressed}\n" +
                                    "changedUp: ${down.changedToUp()}\n" +
                                    "positionChanged: ${down.positionChanged()}\n" +
                                    "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                                    "positionChangeConsumed: ${down.positionChangeConsumed()}\n"


                        eventChanges +=
                            "🍏MOVE\n" +
                                    "changes size ${event.changes.size}\n"
                        gestureText = eventChanges

                        event.changes
                            .forEachIndexed { index: Int, pointerInputChange: PointerInputChange ->


                                // This necessary to prevent other gestures or scrolling
                                // when at least one pointer is down on canvas to draw
                                pointerInputChange.consumePositionChange()

                                eventChanges +=
                                    "Index: " +
                                            "$index, id: ${pointerInputChange.id}, " +
                                            "pos: ${pointerInputChange.position}\n" +
                                            "pressed: ${pointerInputChange.pressed}\n" +
                                            "changedUp: ${pointerInputChange.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${pointerInputChange.changedToUpIgnoreConsumed()}\n" +
                                            "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                                            "positionChangeConsumed: ${pointerInputChange.positionChangeConsumed()}\n" +
                                            "changedToUpIgnoreConsumed: ${pointerInputChange.changedToUpIgnoreConsumed()}\n"
                                gestureText = eventChanges

                            }

                        gestureColor = Green400

                    } while (
                        event.changes.any {
                            it.pressed
                        }
                    )

                    gestureColor = Color.White
                }
            }
        }

    Box(modifier = pointerModifier, contentAlignment = Alignment.Center) {
        Text(
            text = "Touch Here\nto display down or move consume events",
            textAlign = TextAlign.Center
        )
    }

    GestureDisplayBox(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        gestureText = gestureText
    )
}

@Composable
private fun ConsumeDragEventsExample() {

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }
    // This text is drawn to Text composable
    var gestureText by remember { mutableStateOf("") }

    val pointerModifier = gestureModifier
        .background(gestureColor)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down: PointerInputChange = awaitFirstDown().also {
                        gestureColor = Blue400
                    }

                    // Consuming down causes changeToDown to return false
                    // And other events like scroll to not interfere with this event
                    down.consumeDownChange()

                    var eventChanges =
                        "🎃DOWN\n" +
                                "changedToDown: ${down.changedToDown()}, " +
                                "changedToDownIgnoreConsumed: ${down.changedToDownIgnoreConsumed()}\n" +
                                "pressed: ${down.pressed}\n" +
                                "changedUp: ${down.changedToUp()}\n" +
                                "positionChanged: ${down.positionChanged()}\n" +
                                "anyChangeConsumed: ${down.anyChangeConsumed()}\n" +
                                "positionChangeConsumed: ${down.positionChangeConsumed()}\n"

                    gestureText = eventChanges


                    // 🔥 Waits for drag threshold to be passed by pointer
                    // or it returns null if up event is triggered
                    var change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->


                            println(
                                "⛺️ awaitTouchSlopOrCancellation()  " +
                                        "down.id: ${down.id} change.id: ${change.id}" +
                                        "\nposition: ${change.position}, " +
                                        "positionChange: ${change.positionChange()}, over: $over"
                            )

                            // 🔥🔥 If consumePositionChange() is not consumed drag does not
                            // function properly.
                            // Consuming position change causes
                            // change.positionChanged() to return false.
                            change.consumePositionChange()
                            eventChanges +=
                                "⛺️ awaitTouchSlopOrCancellation()\n" +
                                        "down.id: ${down.id} change.id: ${change.id}\n" +
                                        "changedToDown: ${change.changedToDown()}\n" +
                                        "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                        "pressed: ${change.pressed}\n" +
                                        "changedUp: ${change.changedToUp()}\n" +
                                        "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                        "positionChanged: ${change.positionChanged()}\n" +
                                        "anyChangeConsumed: ${change.anyChangeConsumed()}\n" +
                                        "positionChangeConsumed: ${change.positionChangeConsumed()}\n"

                            gestureColor = Brown400
                            gestureText = eventChanges
                        }


                    if (change == null) {
                        gestureColor = Red400
                        gestureText += "awaitTouchSlopOrCancellation() is NULL"
                    } else {

                        while (change != null && change.pressed) {

                            // 🔥 Calls awaitPointerEvent() in a while loop and checks drag change
                            change = awaitDragOrCancellation(change.id)

                            if (change != null && change.pressed) {

                                gestureColor = Green400

                                // 🔥 Consuming position change causes
                                // change.positionChanged() to return false.
                                change.consumePositionChange()

                                eventChanges +=
                                    "🚌 awaitDragOrCancellation()  " +
                                            "down.id: ${down.id} change.id: ${change.id}\n" +
                                            "changedToDown: ${change.changedToDown()}\n" +
                                            "changedToDownIgnoreConsumed: ${change.changedToDownIgnoreConsumed()}\n" +
                                            "pressed: ${change.pressed}\n" +
                                            "changedUp: ${change.changedToUp()}\n" +
                                            "changedToUpIgnoreConsumed: ${change.changedToUpIgnoreConsumed()}\n" +
                                            "positionChanged: ${change.positionChanged()}\n" +
                                            "anyChangeConsumed: ${change.anyChangeConsumed()}\n" +
                                            "positionChangeConsumed: ${change.positionChangeConsumed()}\n"
                            }
                        }

                        gestureText = eventChanges
                        gestureColor = Color.White
                    }
                }
            }
        }

    Box(modifier = pointerModifier, contentAlignment = Alignment.Center) {
        Text(text = "Touch Here\nto display drag consume events", textAlign = TextAlign.Center)
    }

    GestureDisplayBox(
        modifier = gestureTextModifier.verticalScroll(rememberScrollState()),
        gestureText = gestureText
    )

}

private val gestureModifier = Modifier
    .padding(vertical = 8.dp, horizontal = 12.dp)
    .fillMaxWidth()
    .height(90.dp)

private val gestureTextModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .background(BlueGrey400)
    .height(200.dp)
    .padding(2.dp)
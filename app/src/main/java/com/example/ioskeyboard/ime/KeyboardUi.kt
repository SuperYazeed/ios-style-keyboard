package com.example.ioskeyboard.ime

import android.icu.text.UnicodeSet
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyboardRoot(
    onCommitText: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    isPasswordField: () -> Boolean,
    onSwitchToNextIme: () -> Unit
) {
    var shiftState by remember { mutableStateOf(ShiftState.Off) }
    var showSymbols by remember { mutableStateOf(false) }
    var showEmoji by remember { mutableStateOf(false) }
    var lang by remember { mutableStateOf(Lang.AR) }

    val view = LocalView.current
    val haptic: () -> Unit = {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    val layout = when {
        lang == Lang.EN && !showSymbols -> letterLayoutEN()
        lang == Lang.EN && showSymbols -> symbolLayoutEN()
        lang == Lang.AR && !showSymbols -> letterLayoutAR()
        else -> symbolLayoutAR()
    }

    val keyboardBg = Color(0xFFE5E6EA)

    Surface(color = Color.Transparent) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(keyboardBg)
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            AnimatedVisibility(visible = showEmoji) {
                EmojiPanel(
                    onEmojiClick = { emoji ->
                        onCommitText(emoji)
                        haptic()
                    }
                )
            }

            val rows = layout.rows

            CompositionLocalProvider(
                LocalLayoutDirection provides (if (layout.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        row.forEach { key ->
                            Spacer(Modifier.width(4.dp))
                            when (key) {
                                is Key.CharKey -> {
                                    val label = when (shiftState) {
                                        ShiftState.Off -> key.label
                                        ShiftState.On, ShiftState.CapsLock -> key.label.uppercase()
                                    }
                                    val weight = if (key.label == " ") 4f else 1f
                                    KeyButton(
                                        label = label,
                                        primary = true,
                                        weight = weight,
                                        onClick = {
                                            haptic()
                                            val out = when (shiftState) {
                                                ShiftState.Off -> key.value
                                                ShiftState.On, ShiftState.CapsLock -> key.value.uppercase()
                                            }
                                            onCommitText(out)
                                            if (shiftState == ShiftState.On) shiftState = ShiftState.Off
                                        }
                                    )
                                }
                                Key.Shift -> {
                                    KeyIconButton(
                                        text = when (shiftState) {
                                            ShiftState.Off -> "⇧"
                                            ShiftState.On -> "⇧"
                                            ShiftState.CapsLock -> "⇪"
                                        },
                                        onClick = {
                                            haptic()
                                            shiftState = when (shiftState) {
                                                ShiftState.Off -> ShiftState.On
                                                ShiftState.On -> ShiftState.CapsLock
                                                ShiftState.CapsLock -> ShiftState.Off
                                            }
                                        },
                                        onLongClick = { shiftState = ShiftState.CapsLock }
                                    )
                                }
                                Key.Backspace -> {
                                    KeyIconButton(
                                        text = "⌫",
                                        onClick = {
                                            haptic(); onBackspace()
                                        },
                                        onLongClick = {
                                            repeat(5) { onBackspace() }
                                        }
                                    )
                                }
                                Key.Space -> {
                                    val spaceLabel = if (lang == Lang.AR) "مسافة" else "space"
                                    KeyButton(
                                        label = spaceLabel,
                                        primary = true,
                                        weight = 4f,
                                        onClick = { haptic(); onCommitText(" ") }
                                    )
                                }
                                Key.Enter -> {
                                    val label = if (lang == Lang.AR) "إدخال" else "return"
                                    KeyButton(
                                        label = label,
                                        primary = false,
                                        keyColor = Color(0xFFB5B7BD),
                                        weight = 1.6f,
                                        onClick = { haptic(); onEnter() }
                                    )
                                }
                                Key.Switch123 -> {
                                    val label = if (lang == Lang.AR) "١٢٣" else "123"
                                    KeyButton(
                                        label = label,
                                        primary = false,
                                        keyColor = Color(0xFFCDCED3),
                                        weight = 1.2f,
                                        onClick = { haptic(); showSymbols = true }
                                    )
                                }
                                Key.SwitchABC -> {
                                    val label = if (lang == Lang.AR) "أبج" else "ABC"
                                    KeyButton(
                                        label = label,
                                        primary = false,
                                        keyColor = Color(0xFFCDCED3),
                                        weight = 1.2f,
                                        onClick = { haptic(); showSymbols = false }
                                    )
                                }
                                Key.Emoji -> {
                                    KeyButton(
                                        label = if (showEmoji) "🙂" else "😊",
                                        primary = false,
                                        keyColor = Color(0xFFCDCED3),
                                        weight = 1f,
                                        onClick = { haptic(); showEmoji = !showEmoji }
                                    )
                                }
                                Key.Globe -> {
                                    KeyButton(
                                        label = "🌐",
                                        primary = false,
                                        keyColor = Color(0xFFCDCED3),
                                        weight = 1f,
                                        onClick = {
                                            haptic()
                                            lang = if (lang == Lang.AR) Lang.EN else Lang.AR
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    primary: Boolean,
    keyColor: Color = if (primary) Color.White else Color(0xFFCDCED3),
    weight: Float = 1f,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .weight(weight, fill = true)
            .height(48.dp)
            .shadow(
                elevation = if (primary) 2.dp else 0.dp,
                shape = shape,
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(keyColor, shape)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeyIconButton(
    text: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .height(48.dp)
            .width(56.dp)
            .background(Color(0xFFCDCED3), shape)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = { onLongClick?.invoke() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 18.sp, color = Color.Black)
    }
}

@Composable
private fun EmojiPanel(
    onEmojiClick: (String) -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp, max = 260.dp)
            .background(Color.White, shape)
            .padding(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("\u0627\u0644\u0643\u0644","\u0627\u0628\u062a\u0633\u0627\u0645\u0627\u062a","\u0623\u0639\u0644\u0627\u0645","\u0631\u0645\u0648\u0632").forEach { name ->
                AssistChip(onClick = { }, label = { Text(name, fontSize = 12.sp) })
            }
        }
        Spacer(Modifier.height(8.dp))
        val allEmoji by remember { mutableStateOf(generateEmojiList()) }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 36.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(allEmoji) { e ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF5F6F7), RoundedCornerShape(8.dp))
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onEmojiClick(e) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(e, fontSize = 20.sp)
                }
            }
        }
    }
}

private fun generateEmojiList(): List<String> {
    val list = mutableListOf<String>()
    try {
        val set = UnicodeSet("[[:Emoji:]]")
        val ranges = set.rangeCount
        for (i in 0 until ranges) {
            val start = set.getRangeStart(i)
            val end = set.getRangeEnd(i)
            var cp = start
            while (cp <= end) {
                if (!Character.isISOControl(cp)) {
                    val s = String(Character.toChars(cp))
                    list.add(s)
                }
                cp++
            }
        }
    } catch (t: Throwable) {
        list += listOf("\uD83D\uDE00","\uD83D\uDE01","\uD83D\uDE02","\uD83E\uDD23","\uD83D\uDE03","\uD83D\uDE04","\uD83D\uDE09","\uD83D\uDE0A","\uD83D\uDE0D","\uD83D\uDE18","\uD83D\uDE1C","\uD83E\uDD14","\uD83E\uDD17","\uD83D\uDE0E")
    }
    val keycaps = listOf("#","*","0","1","2","3","4","5","6","7","8","9").map { it + "\uFE0F\u20E3" }
    list.addAll(keycaps)
    val A = 0x1F1E6
    for (i in 0 until 26) {
        for (j in 0 until 26) {
            val flag = String(Character.toChars(A + i)) + String(Character.toChars(A + j))
            list.add(flag)
        }
    }
    return list.distinct()
}

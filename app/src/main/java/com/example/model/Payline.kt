package com.example.model

import androidx.compose.ui.graphics.Color

data class GridPos(val col: Int, val row: Int)

enum class Payline(
    val id: Int,
    val lineName: String,
    val positions: List<GridPos>,
    val lineColor: Color
) {
    LINE_1(
        id = 1,
        lineName = "Линия 1 (Центр)",
        positions = listOf(GridPos(0, 1), GridPos(1, 1), GridPos(2, 1)),
        lineColor = Color(0xFFFF2A4B) // Bright Red
    ),
    LINE_2(
        id = 2,
        lineName = "Линия 2 (Верх)",
        positions = listOf(GridPos(0, 0), GridPos(1, 0), GridPos(2, 0)),
        lineColor = Color(0xFF00E5FF) // Cyan
    ),
    LINE_3(
        id = 3,
        lineName = "Линия 3 (Низ)",
        positions = listOf(GridPos(0, 2), GridPos(1, 2), GridPos(2, 2)),
        lineColor = Color(0xFF00FF66) // Neon Green
    ),
    LINE_4(
        id = 4,
        lineName = "Линия 4 (Диагональ \\)",
        positions = listOf(GridPos(0, 0), GridPos(1, 1), GridPos(2, 2)),
        lineColor = Color(0xFFFFD700) // Gold
    ),
    LINE_5(
        id = 5,
        lineName = "Линия 5 (Диагональ /)",
        positions = listOf(GridPos(0, 2), GridPos(1, 1), GridPos(2, 0)),
        lineColor = Color(0xFFFF007F) // Magenta
    );

    companion object {
        val ALL_LINES = entries.toList()
        fun getActiveLines(count: Int): List<Payline> = ALL_LINES.take(count.coerceIn(1, 5))
    }
}

package com.example.model

enum class WinTier(val titleRu: String, val multiplierThreshold: Double) {
    NONE("", 0.0),
    REGULAR("ВЫИГРЫШ!", 1.0),
    BIG_WIN("БОЛЬШОЙ КУШ! 💥", 15.0),
    MEGA_WIN("МЕГА ВИН! 🌟", 50.0),
    JACKPOT_777("ДЖЕКПОТ 777! 🎰🔥", 200.0)
}

data class LineWin(
    val payline: Payline,
    val matchedSymbol: SlotSymbol,
    val matchCount: Int,
    val linePayout: Long,
    val winningPositions: List<GridPos>
)

data class SpinEvaluation(
    val grid: List<List<SlotSymbol>>, // grid[col][row]
    val lineWins: List<LineWin>,
    val totalWinCoins: Long,
    val totalBet: Long,
    val winTier: WinTier,
    val isJackpot777: Boolean,
    val freeSpinsAwarded: Int,
    val scatterCount: Int,
    val appliedModifier: SlotModifier = SlotModifier.NONE
)

object SlotEvaluator {

    /**
     * Evaluates a grid according to the selected mode (3x1, 3x3, 9x5)
     */
    fun evaluateSpin(
        grid: List<List<SlotSymbol>>,
        activeLines: List<Payline>,
        betPerLine: Long,
        freeSpinMultiplier: Int = 1,
        gridMode: SlotGridMode = SlotGridMode.GRID_3X3,
        modifier: SlotModifier = SlotModifier.NONE
    ): SpinEvaluation {
        val lineWins = mutableListOf<LineWin>()
        var rawPayout: Long = 0
        var isJackpot777 = false
        var scatterCount = 0

        val cols = grid.size
        val rows = if (cols > 0) grid[0].size else 0

        // Count scatters
        for (c in 0 until cols) {
            for (r in 0 until rows) {
                if (grid[c][r].isScatter) {
                    scatterCount++
                }
            }
        }

        when (gridMode) {
            SlotGridMode.GRID_3X1 -> {
                // Single center line (3 reels, 1 row)
                val s0 = grid[0][0]
                val s1 = grid[1][0]
                val s2 = grid[2][0]

                val match = checkLineMatch3(s0, s1, s2)
                if (match != null) {
                    val payout = match.payout3 * betPerLine * freeSpinMultiplier
                    rawPayout += payout
                    if (match == SlotSymbol.SEVEN_RED || match == SlotSymbol.SEVEN_GOLD) {
                        isJackpot777 = true
                    }
                    lineWins.add(
                        LineWin(
                            payline = Payline.LINE_1,
                            matchedSymbol = match,
                            matchCount = 3,
                            linePayout = payout,
                            winningPositions = listOf(GridPos(0, 0), GridPos(1, 0), GridPos(2, 0))
                        )
                    )
                } else if ((s0 == SlotSymbol.CHERRY || s0 == SlotSymbol.WILD) &&
                    (s1 == SlotSymbol.CHERRY || s1 == SlotSymbol.WILD)
                ) {
                    val payout = SlotSymbol.CHERRY.payout2 * betPerLine * freeSpinMultiplier
                    rawPayout += payout
                    lineWins.add(
                        LineWin(
                            payline = Payline.LINE_1,
                            matchedSymbol = SlotSymbol.CHERRY,
                            matchCount = 2,
                            linePayout = payout,
                            winningPositions = listOf(GridPos(0, 0), GridPos(1, 0))
                        )
                    )
                }
            }

            SlotGridMode.GRID_3X3 -> {
                for (line in activeLines) {
                    val s0 = grid[line.positions[0].col][line.positions[0].row]
                    val s1 = grid[line.positions[1].col][line.positions[1].row]
                    val s2 = grid[line.positions[2].col][line.positions[2].row]

                    val match = checkLineMatch3(s0, s1, s2)
                    if (match != null) {
                        val payout = match.payout3 * betPerLine * freeSpinMultiplier
                        rawPayout += payout
                        if (match == SlotSymbol.SEVEN_RED || match == SlotSymbol.SEVEN_GOLD) {
                            isJackpot777 = true
                        }
                        lineWins.add(
                            LineWin(
                                payline = line,
                                matchedSymbol = match,
                                matchCount = 3,
                                linePayout = payout,
                                winningPositions = line.positions
                            )
                        )
                    } else if ((s0 == SlotSymbol.CHERRY || s0 == SlotSymbol.WILD) &&
                        (s1 == SlotSymbol.CHERRY || s1 == SlotSymbol.WILD)
                    ) {
                        val payout = SlotSymbol.CHERRY.payout2 * betPerLine * freeSpinMultiplier
                        rawPayout += payout
                        lineWins.add(
                            LineWin(
                                payline = line,
                                matchedSymbol = SlotSymbol.CHERRY,
                                matchCount = 2,
                                linePayout = payout,
                                winningPositions = listOf(line.positions[0], line.positions[1])
                            )
                        )
                    }
                }
            }

            SlotGridMode.GRID_9X5 -> {
                // 9 columns x 5 rows: check horizontal rows and cluster patterns
                for (r in 0 until minOf(rows, 5)) {
                    // Check consecutive matching symbols from reel 0
                    val firstSym = grid[0][r]
                    var matchCount = 1
                    val winningPos = mutableListOf(GridPos(0, r))

                    for (c in 1 until minOf(cols, 9)) {
                        val curr = grid[c][r]
                        if (curr == firstSym || curr == SlotSymbol.WILD || (firstSym == SlotSymbol.WILD && !curr.isScatter)) {
                            matchCount++
                            winningPos.add(GridPos(c, r))
                        } else {
                            break
                        }
                    }

                    if (matchCount >= 3) {
                        val multiplier: Long = when (matchCount) {
                            3 -> firstSym.payout3.toLong()
                            4 -> (firstSym.payout3.toDouble() * 1.5).toLong()
                            5 -> firstSym.payout3.toLong() * 2L
                            6 -> firstSym.payout3.toLong() * 3L
                            7 -> firstSym.payout3.toLong() * 5L
                            8 -> firstSym.payout3.toLong() * 8L
                            else -> firstSym.payout3.toLong() * 15L
                        }
                        val payout = multiplier * betPerLine * freeSpinMultiplier
                        rawPayout += payout
                        if ((firstSym == SlotSymbol.SEVEN_RED || firstSym == SlotSymbol.SEVEN_GOLD) && matchCount >= 5) {
                            isJackpot777 = true
                        }
                        lineWins.add(
                            LineWin(
                                payline = Payline.LINE_1,
                                matchedSymbol = firstSym,
                                matchCount = matchCount,
                                linePayout = payout,
                                winningPositions = winningPos
                            )
                        )
                    }
                }
            }
        }

        // Scatter bonus
        var freeSpinsAwarded = 0
        if (scatterCount >= 3) {
            freeSpinsAwarded = 10
            rawPayout += (50L * betPerLine * maxOf(1, activeLines.size) * freeSpinMultiplier)
        }

        // Apply slot modifier (buff e.g. x2, x3, x7 or debuff x0.5)
        val finalPayout = if (rawPayout > 0) {
            (rawPayout * modifier.multiplier).toLong().coerceAtLeast(1L)
        } else 0L

        val totalBet = when (gridMode) {
            SlotGridMode.GRID_3X1 -> betPerLine
            SlotGridMode.GRID_3X3 -> betPerLine * activeLines.size
            SlotGridMode.GRID_9X5 -> betPerLine * 10 // Fixed 10 way bet
        }

        val winMultiplier = if (totalBet > 0) finalPayout.toDouble() / totalBet.toDouble() else 0.0

        val winTier = when {
            isJackpot777 -> WinTier.JACKPOT_777
            winMultiplier >= WinTier.MEGA_WIN.multiplierThreshold -> WinTier.MEGA_WIN
            winMultiplier >= WinTier.BIG_WIN.multiplierThreshold -> WinTier.BIG_WIN
            finalPayout > 0 -> WinTier.REGULAR
            else -> WinTier.NONE
        }

        return SpinEvaluation(
            grid = grid,
            lineWins = lineWins,
            totalWinCoins = finalPayout,
            totalBet = totalBet,
            winTier = winTier,
            isJackpot777 = isJackpot777,
            freeSpinsAwarded = freeSpinsAwarded,
            scatterCount = scatterCount,
            appliedModifier = modifier
        )
    }

    private fun checkLineMatch3(s0: SlotSymbol, s1: SlotSymbol, s2: SlotSymbol): SlotSymbol? {
        if (s0.isScatter || s1.isScatter || s2.isScatter) return null
        if (s0 == s1 && s1 == s2) return s0
        val nonWilds = listOf(s0, s1, s2).filter { !it.isWild }
        if (nonWilds.isEmpty()) return SlotSymbol.WILD
        val target = nonWilds.first()
        return if (nonWilds.all { it == target }) target else null
    }
}

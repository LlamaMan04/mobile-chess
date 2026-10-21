package com.example.mobilechess

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BoardLight = Color(0xFFF0D9B5)
private val BoardDark = Color(0xFFB58863)
private val Selection = Color(0xFF5B9A6B)

// Draws the complete 8x8 board and forwards each square tap to the game screen.
@Composable
fun ChessBoard(
    board: List<List<Piece?>>,
    selected: Square?,
    targets: Set<Square>,
    onSquare: (Square) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        // Give every row and square equal space so the board remains square on any screen.
        repeat(8) { row ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(8) { col ->
                    BoardSquare(
                        modifier = Modifier.weight(1f),
                        board = board,
                        square = Square(row, col),
                        selected = selected,
                        targets = targets,
                        onSquare = onSquare,
                    )
                }
            }
        }
    }
}

// Renders one board square, including its color, selection highlight, target marker, and piece.
@Composable
private fun BoardSquare(
    modifier: Modifier,
    board: List<List<Piece?>>,
    square: Square,
    selected: Square?,
    targets: Set<Square>,
    onSquare: (Square) -> Unit,
) {
    val piece = board[square.row][square.col]
    val selectedHere = square == selected
    val target = square in targets

    // The square itself is clickable; the parent decides whether the tap selects or moves a piece.
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if ((square.row + square.col) % 2 == 0) BoardLight else BoardDark,
            )
            .clickable { onSquare(square) },
        contentAlignment = Alignment.Center,
    ) {
        if (selectedHere) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Selection.copy(alpha = 0.72f)),
            )
        }

        if (target) {
            Box(
                modifier = Modifier
                    .size(if (piece == null) 12.dp else 40.dp)
                    .clip(CircleShape)
                    .background(
                        Selection.copy(alpha = if (piece == null) 0.75f else 0.4f),
                    ),
            )
        }

        piece?.let {
            Text(
                text = it.symbol,
                fontSize = 38.sp,
                color = if (it.side == Side.WHITE) Color.White else Navy,
            )
        }
    }
}

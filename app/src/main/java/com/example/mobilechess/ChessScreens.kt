package com.example.mobilechess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val Navy = Color(0xFF102A43)
private val Slate = Color(0xFF486581)
private val Sand = Color(0xFFF4E9D8)
private val Selection = Color(0xFF5B9A6B)

@Composable
fun ChessApp() {
    var showingGame by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Sand,
    ) {
        if (showingGame) {
            GameScreen(onBack = { showingGame = false })
        } else {
            MenuScreen(onStart = { showingGame = true })
        }
    }
}

@Composable
private fun MenuScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "MOBILE",
            color = Slate,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
        )
        Text(
            text = "CHESS",
            color = Navy,
            fontSize = 52.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
        )
        Spacer(modifier = Modifier.height(26.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Two players. One board.",
                    style = MaterialTheme.typography.titleLarge,
                    color = Navy,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pass the device after every move and take turns building your strategy.",
                    textAlign = TextAlign.Center,
                    color = Slate,
                )
                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy),
                ) {
                    Text(
                        text = "START A NEW GAME",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Select a piece, then select its destination.",
            color = Slate,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun GameScreen(onBack: () -> Unit) {
    var board by remember { mutableStateOf(initialBoard()) }
    var turn by remember { mutableStateOf(Side.WHITE) }
    var selected by remember { mutableStateOf<Square?>(null) }
    val legalTargets = selected?.let { targetsFor(it, board) }.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = onBack,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text("‹  MENU")
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "LOCAL GAME",
                    color = Slate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = if (turn == Side.WHITE) "White's turn" else "Black's turn",
                    color = Navy,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        PlayerCard("Black", turn == Side.BLACK, "♚")
        Spacer(modifier = Modifier.height(14.dp))

        ChessBoard(
            board = board,
            selected = selected,
            targets = legalTargets,
        ) { square ->
            val piece = board[square.row][square.col]

            when {
                selected == null && piece?.side == turn -> selected = square
                selected != null && square in legalTargets -> {
                    board = move(board, selected!!, square)
                    selected = null
                    turn = if (turn == Side.WHITE) Side.BLACK else Side.WHITE
                }
                piece?.side == turn -> selected = square
                else -> selected = null
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        PlayerCard("White", turn == Side.WHITE, "♔")
        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                board = initialBoard()
                turn = Side.WHITE
                selected = null
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("↻  RESET GAME")
        }
    }
}

@Composable
private fun PlayerCard(
    name: String,
    active: Boolean,
    piece: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) Color.White else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (active) Navy else Slate),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = piece,
                color = Color.White,
                fontSize = 22.sp,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$name player",
            color = Navy,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(modifier = Modifier.weight(1f))

        if (active) {
            Text(
                text = "TO MOVE",
                color = Selection,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

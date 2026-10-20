package com.example.mobilechess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilechess.ui.theme.MobileChessTheme

private val Navy = Color(0xFF102A43)
private val Slate = Color(0xFF486581)
private val Sand = Color(0xFFF4E9D8)
private val BoardLight = Color(0xFFF0D9B5)
private val BoardDark = Color(0xFFB58863)
private val Selection = Color(0xFF5B9A6B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MobileChessTheme { ChessApp() } }
    }
}

private enum class Side { WHITE, BLACK }
private enum class PieceType(val white: String, val black: String) {
    KING("♔", "♚"), QUEEN("♕", "♛"), ROOK("♖", "♜"), BISHOP("♗", "♝"), KNIGHT("♘", "♞"), PAWN("♙", "♟")
}
private data class Piece(val type: PieceType, val side: Side) { val symbol get() = if (side == Side.WHITE) type.white else type.black }
private data class Square(val row: Int, val col: Int)

@Composable
private fun ChessApp() {
    var showingGame by remember { mutableStateOf(false) }
    Surface(Modifier.fillMaxSize(), color = Sand) {
        if (showingGame) GameScreen { showingGame = false } else MenuScreen { showingGame = true }
    }
}

@Composable
private fun MenuScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("MOBILE", color = Slate, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        Text("CHESS", color = Navy, fontSize = 52.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Spacer(Modifier.height(26.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Two players. One board.", style = MaterialTheme.typography.titleLarge, color = Navy, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Pass the device after every move and take turns building your strategy.", textAlign = TextAlign.Center, color = Slate)
                Spacer(Modifier.height(22.dp))
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Navy)) { Text("START A NEW GAME", fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("Select a piece, then select its destination.", color = Slate, fontSize = 14.sp)
    }
}

@Composable
private fun GameScreen(onBack: () -> Unit) {
    var board by remember { mutableStateOf(initialBoard()) }
    var turn by remember { mutableStateOf(Side.WHITE) }
    var selected by remember { mutableStateOf<Square?>(null) }
    val legalTargets = selected?.let { targetsFor(it, board) }.orEmpty()
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) { Text("‹  MENU") }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("LOCAL GAME", color = Slate, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(if (turn == Side.WHITE) "White's turn" else "Black's turn", color = Navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(22.dp))
        PlayerCard("Black", turn == Side.BLACK, "♚")
        Spacer(Modifier.height(14.dp))
        ChessBoard(board, selected, legalTargets) { square ->
            val piece = board[square.row][square.col]
            when {
                selected == null && piece?.side == turn -> selected = square
                selected != null && square in legalTargets -> { board = move(board, selected!!, square); selected = null; turn = if (turn == Side.WHITE) Side.BLACK else Side.WHITE }
                piece?.side == turn -> selected = square
                else -> selected = null
            }
        }
        Spacer(Modifier.height(14.dp))
        PlayerCard("White", turn == Side.WHITE, "♔")
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = { board = initialBoard(); turn = Side.WHITE; selected = null }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("↻  RESET GAME") }
    }
}

@Composable
private fun PlayerCard(name: String, active: Boolean, piece: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (active) Color.White else Color.Transparent).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(if (active) Navy else Slate), contentAlignment = Alignment.Center) { Text(piece, color = Color.White, fontSize = 22.sp) }
        Spacer(Modifier.width(10.dp)); Text("$name player", color = Navy, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
        Spacer(Modifier.weight(1f)); if (active) Text("TO MOVE", color = Selection, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ChessBoard(board: List<List<Piece?>>, selected: Square?, targets: Set<Square>, onSquare: (Square) -> Unit) {
    Column(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp))) {
        repeat(8) { row -> Row(Modifier.weight(1f)) { repeat(8) { col ->
            val square = Square(row, col); val selectedHere = square == selected; val target = square in targets
            Box(Modifier.weight(1f).fillMaxSize().background(if ((row + col) % 2 == 0) BoardLight else BoardDark).clickable { onSquare(square) }, contentAlignment = Alignment.Center) {
                if (selectedHere) Box(Modifier.size(36.dp).clip(CircleShape).background(Selection.copy(alpha = .72f)))
                if (target) Box(Modifier.size(if (board[row][col] == null) 12.dp else 40.dp).clip(CircleShape).background(Selection.copy(alpha = if (board[row][col] == null) .75f else .4f)))
                board[row][col]?.let { Text(it.symbol, fontSize = 38.sp, color = if (it.side == Side.WHITE) Color.White else Navy) }
            }
        } } }
    }
}

private fun initialBoard(): List<List<Piece?>> = listOf(
    listOf(Piece(PieceType.ROOK, Side.BLACK), Piece(PieceType.KNIGHT, Side.BLACK), Piece(PieceType.BISHOP, Side.BLACK), Piece(PieceType.QUEEN, Side.BLACK), Piece(PieceType.KING, Side.BLACK), Piece(PieceType.BISHOP, Side.BLACK), Piece(PieceType.KNIGHT, Side.BLACK), Piece(PieceType.ROOK, Side.BLACK)),
    List(8) { Piece(PieceType.PAWN, Side.BLACK) }, List(8) { null }, List(8) { null }, List(8) { null }, List(8) { null }, List(8) { Piece(PieceType.PAWN, Side.WHITE) },
    listOf(Piece(PieceType.ROOK, Side.WHITE), Piece(PieceType.KNIGHT, Side.WHITE), Piece(PieceType.BISHOP, Side.WHITE), Piece(PieceType.QUEEN, Side.WHITE), Piece(PieceType.KING, Side.WHITE), Piece(PieceType.BISHOP, Side.WHITE), Piece(PieceType.KNIGHT, Side.WHITE), Piece(PieceType.ROOK, Side.WHITE))
)

private fun move(board: List<List<Piece?>>, from: Square, to: Square) = board.mapIndexed { row, items -> items.mapIndexed { col, piece -> when { row == to.row && col == to.col -> board[from.row][from.col]; row == from.row && col == from.col -> null; else -> piece } } }

private fun targetsFor(from: Square, board: List<List<Piece?>>): Set<Square> {
    val piece = board[from.row][from.col] ?: return emptySet(); val options = mutableSetOf<Square>()
    fun add(row: Int, col: Int) { if (row in 0..7 && col in 0..7 && board[row][col]?.side != piece.side) options += Square(row, col) }
    fun slide(directions: List<Pair<Int, Int>>) = directions.forEach { (dr, dc) -> var r = from.row + dr; var c = from.col + dc; while (r in 0..7 && c in 0..7) { if (board[r][c]?.side == piece.side) break; options += Square(r, c); if (board[r][c] != null) break; r += dr; c += dc } }
    when (piece.type) {
        PieceType.PAWN -> { val step = if (piece.side == Side.WHITE) -1 else 1; val start = if (piece.side == Side.WHITE) 6 else 1; if ((from.row + step) in 0..7 && board[from.row + step][from.col] == null) { options += Square(from.row + step, from.col); if (from.row == start && board[from.row + 2 * step][from.col] == null) options += Square(from.row + 2 * step, from.col) }; listOf(-1, 1).forEach { dc -> if ((from.col + dc) in 0..7 && (from.row + step) in 0..7 && board[from.row + step][from.col + dc]?.side?.let { it != piece.side } == true) options += Square(from.row + step, from.col + dc) } }
        PieceType.KNIGHT -> listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1).forEach { add(from.row + it.first, from.col + it.second) }
        PieceType.BISHOP -> slide(listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1))
        PieceType.ROOK -> slide(listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1))
        PieceType.QUEEN -> slide(listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1, -1 to 0, 1 to 0, 0 to -1, 0 to 1))
        PieceType.KING -> for (r in -1..1) for (c in -1..1) if (r != 0 || c != 0) add(from.row + r, from.col + c)
    }
    return options
}

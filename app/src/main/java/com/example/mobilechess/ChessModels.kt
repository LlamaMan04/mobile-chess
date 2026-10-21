package com.example.mobilechess

// Identifies which player owns a piece or whose turn it is.
enum class Side {
    WHITE,
    BLACK,
}

// The supported chess pieces and the Unicode symbols used to draw them.
enum class PieceType(
    val whiteSymbol: String,
    val blackSymbol: String,
) {
    KING("♔", "♚"),
    QUEEN("♕", "♛"),
    ROOK("♖", "♜"),
    BISHOP("♗", "♝"),
    KNIGHT("♘", "♞"),
    PAWN("♙", "♟"),
}

// A piece combines a type (for example, knight) with its player/color.
data class Piece(
    val type: PieceType,
    val side: Side,
) {
    // Select the correct white or black symbol whenever the piece is displayed.
    val symbol: String
        get() = if (side == Side.WHITE) type.whiteSymbol else type.blackSymbol
}

// A board location represented by a zero-based row and column.
data class Square(
    val row: Int,
    val col: Int,
)

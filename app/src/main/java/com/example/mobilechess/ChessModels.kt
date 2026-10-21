package com.example.mobilechess

enum class Side {
    WHITE,
    BLACK,
}

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

data class Piece(
    val type: PieceType,
    val side: Side,
) {
    val symbol: String
        get() = if (side == Side.WHITE) type.whiteSymbol else type.blackSymbol
}

data class Square(
    val row: Int,
    val col: Int,
)

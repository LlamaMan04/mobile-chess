package com.example.mobilechess

// Creates the standard chess starting position. A null entry represents an empty square.
fun initialBoard(): List<List<Piece?>> = listOf(
    listOf(
        Piece(PieceType.ROOK, Side.BLACK),
        Piece(PieceType.KNIGHT, Side.BLACK),
        Piece(PieceType.BISHOP, Side.BLACK),
        Piece(PieceType.QUEEN, Side.BLACK),
        Piece(PieceType.KING, Side.BLACK),
        Piece(PieceType.BISHOP, Side.BLACK),
        Piece(PieceType.KNIGHT, Side.BLACK),
        Piece(PieceType.ROOK, Side.BLACK),
    ),
    List(8) { Piece(PieceType.PAWN, Side.BLACK) },
    List(8) { null },
    List(8) { null },
    List(8) { null },
    List(8) { null },
    List(8) { Piece(PieceType.PAWN, Side.WHITE) },
    listOf(
        Piece(PieceType.ROOK, Side.WHITE),
        Piece(PieceType.KNIGHT, Side.WHITE),
        Piece(PieceType.BISHOP, Side.WHITE),
        Piece(PieceType.QUEEN, Side.WHITE),
        Piece(PieceType.KING, Side.WHITE),
        Piece(PieceType.BISHOP, Side.WHITE),
        Piece(PieceType.KNIGHT, Side.WHITE),
        Piece(PieceType.ROOK, Side.WHITE),
    ),
)

// Returns a new board with the piece moved from one square to another.
// The original board is left unchanged, which makes state updates predictable in Compose.
fun move(
    board: List<List<Piece?>>,
    from: Square,
    to: Square,
): List<List<Piece?>> = board.mapIndexed { row, items ->
    items.mapIndexed { col, piece ->
        when {
            row == to.row && col == to.col -> board[from.row][from.col]
            row == from.row && col == from.col -> null
            else -> piece
        }
    }
}

// Calculates the squares a selected piece can reach using the app's simplified chess rules.
fun targetsFor(
    from: Square,
    board: List<List<Piece?>>,
): Set<Square> {
    val piece = board[from.row][from.col] ?: return emptySet()
    val options = mutableSetOf<Square>()

    // Add one destination if it is on the board and is empty or occupied by an opponent.
    fun add(row: Int, col: Int) {
        if (row in 0..7 && col in 0..7 && board[row][col]?.side != piece.side) {
            options += Square(row, col)
        }
    }

    // Walk in each direction until the edge of the board or another piece is reached.
    // This is shared by bishops, rooks, and queens.
    fun slide(directions: List<Pair<Int, Int>>) {
        directions.forEach { (rowDelta, colDelta) ->
            var row = from.row + rowDelta
            var col = from.col + colDelta

            while (row in 0..7 && col in 0..7) {
                if (board[row][col]?.side == piece.side) {
                    break
                }
                options += Square(row, col)
                if (board[row][col] != null) {
                    break
                }
                row += rowDelta
                col += colDelta
            }
        }
    }

    // Each piece type has its own movement pattern.
    when (piece.type) {
        PieceType.PAWN -> {
            // Pawns move forward, may move two squares from their starting row,
            // and capture diagonally when an opposing piece is present.
            val step = if (piece.side == Side.WHITE) -1 else 1
            val startRow = if (piece.side == Side.WHITE) 6 else 1
            val nextRow = from.row + step

            if (nextRow in 0..7 && board[nextRow][from.col] == null) {
                options += Square(nextRow, from.col)
                val doubleStepRow = from.row + 2 * step
                if (from.row == startRow && board[doubleStepRow][from.col] == null) {
                    options += Square(doubleStepRow, from.col)
                }
            }

            listOf(-1, 1).forEach { colDelta ->
                val targetCol = from.col + colDelta
                if (targetCol in 0..7 && nextRow in 0..7) {
                    val targetPiece = board[nextRow][targetCol]
                    if (targetPiece?.side?.let { it != piece.side } == true) {
                        options += Square(nextRow, targetCol)
                    }
                }
            }
        }

        PieceType.KNIGHT -> {
            // Knights jump in eight possible L-shaped patterns.
            listOf(
                -2 to -1,
                -2 to 1,
                -1 to -2,
                -1 to 2,
                1 to -2,
                1 to 2,
                2 to -1,
                2 to 1,
            ).forEach { (rowDelta, colDelta) ->
                add(from.row + rowDelta, from.col + colDelta)
            }
        }

        PieceType.BISHOP -> slide(
            // Bishops move diagonally until blocked.
            listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1),
        )

        PieceType.ROOK -> slide(
            // Rooks move horizontally or vertically until blocked.
            listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1),
        )

        PieceType.QUEEN -> slide(
            // Queens combine the bishop's diagonals with the rook's straight lines.
            listOf(
                -1 to -1,
                -1 to 1,
                1 to -1,
                1 to 1,
                -1 to 0,
                1 to 0,
                0 to -1,
                0 to 1,
            ),
        )

        PieceType.KING -> {
            // Kings move one square in any direction.
            for (rowDelta in -1..1) {
                for (colDelta in -1..1) {
                    if (rowDelta != 0 || colDelta != 0) {
                        add(from.row + rowDelta, from.col + colDelta)
                    }
                }
            }
        }
    }

    return options
}

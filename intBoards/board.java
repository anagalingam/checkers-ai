 class board {
    
    // Each of 32 squares is 3 bits: ( king? | player? | occupied? )
    // 0    Empty
    // 1    P1's Normal Piece
    // 2    P0's Normal Piece
    // 3    P1's King
    // 4    P0's King
    //
    //  Transformation of game board from
    //
    //      0       1       2       3
    //  4       5       6       7
    //      8       9       10      11
    //  12      13      14      15
    //      16      17      18      19
    //  20      21      22      23
    //      24      25      26      27
    //  28      29      30      31
    //
    //  To:
    //
    //  UP SIDE = P1
    //  
    //  28  29  30  31  \
    //                    = boardState[3]
    //  24  25  26  27  /
    //
    //  20  21  22  23  \
    //                    = boardState[2]
    //  16  17  18  19  /
    //
    //  12  13  14  15  \
    //                    = boardState[1]
    //  8   9   10  11  /
    //  
    //  4   5   6   7   \
    //                    = boardState[0]
    //  0   1   2   3   /
    //
    //  DOWN SIDE = P0
    //
    //  Each int represents 2 rows, bits 0-2 for downleft           (ex. 0)
    //                              bits 3-5 for downleft + right 1 (ex. 1)
    //                              bits 21-23 for upright          (ex. 7)
    //
    //  Each valid move is expressed as an array of length maxJumps+2.
    //  [ startPos , endPos , jump0 , jump1, ... , jump_maxJumps-1 ]
    //  A matrix of all valid moves for each player in the given boardState is stored

    private int[] boardState;
    private final static int maxValidMoves = 1000;  // Overestimating. Each piece would have on avg 41.667 valid moves
    private final static int maxJumps = 9;          // Looked up online
    private int[][] validMovesP0;
    private int[][] validMovesP1;
    private int numValidMovesP0 = 0;
    private int numValidMovesP1 = 0;
  
    // Zero parameter constructor creates a "new game" board
    public board() {
        // Int Array
        // 7190235 => Not King, Player 1, Occupied all 4 cols, 2 rows
        boardState = { 7190235, 1755 , 2396160, 2396745 };
        validMovesP0 = new int[maxValidMoves][maxJumps+2];
        validMovesP1 = new int[maxValidMoves][maxJumps+2];
        
        // Char Array
        // 1755 => Not King, Player 1, Occupied all 4 columns
        // 585  => Not King, Player 0, Occupied all 4 columns
        //boardState = {1755, 1755, 1755, 0, 0, 585, 585, 585};
    }
    
    // inputBoard must be checked by caller
    public board(int[] inputBoard) {
        boardState = inputBoard;
        validMovesP0 = new int[maxValidMoves][maxJumps+2];
        validMovesP1 = new int[maxValidMoves][maxJumps+2];
    }
    
    // Return 3 bit value at specified square
    private int getSquareVal( int squareNum ) {
        return (boardState[squareNum/4]&(7 << (squareNum&7)*3)) >> (squareNum&7)*3;
    }

    // If the desired square DNE, return -1
    // Direction of desired square based on the function (Up, UpAdj, Down, DownAdj)

    // getSquareUp/Down are simple getSquare same column, row above/below.
    private int getSquareDown( int squareNum ) {
        return (squareNum>3) ? squareNum-4 : -1;
    }
    
    private int getSquareUp( int squareNum ) {
        return (squareNum < 28) ? squareNum+4 : -1;
    }


    // getSquareAdj are more tricky since they must also check if the column exists too.
    // The column to look at changes based on row even vs odd
    private int getSquareDownAdj( int squareNum ) {
        if( ((squareNum/4)&1) == 0 )    // Even row, look right 1 column
            return (((squareNum&3) == 3) || (squareNum < 4)) ? -1 : squareNum-3;
        return ((squareNum&3)==0) ? -1 : squareNum-5;   // Odd row, look left 1 column
    }

    private int getSquareUpAdj( int squareNum ) {
        if( ((squareNum/4)&1) == 1 )    // Odd row, look left 1 column
            return (((squareNum&3) == 0) || squareNum>27) ? -1 : squareNum+3;
        return ((squareNum&3) == 3) ? -1 : squareNum-3;
    }

    public void updateValidMovesP0() {
        int currSqVal;
        for( int sq = 0; sq < 32; sq++ ) {
            currSqVal = getSquare(sq);
            if( (currSqVal&3) == 1 ) {      // If occupied and Player0's piece
                if( currSqVal == 7 ) {      // If king
                }
                else {                      // Not a king
                    if( getSquareVal(getSquareDown(sq)) == 0 ) {    //Empty
                        validMovesP0[numValidMovesP0][0] = sq;
                        validMovesP0[numValidMovesP0][1] = getSquareDown(sq);
                        numValidMovesP0++;
                    }
                    else if( getSquareVal(getSquareDown(sq)) > 2) {     //Player1's piece

                    if( (getSquareVal(getSquareDownAdj(sq))&1)
                    getSquareDownAdj(sq);

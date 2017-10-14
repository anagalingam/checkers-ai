 class board {
    
    // Each of 32 squares is 3 bits: ( king? | player? | occupied? )
    // xx0  =>  empty square
    // xx1  =>  occupied square
    // x0x  =>  player 0's piece
    // x1x  =>  player 1's piece
    // 0xx  =>  NOT a king
    // 1xx  =>  king
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
    //  UP SIDE = P0
    //
    //  0   1   2   3   \
    //                    = boardState[0]
    //  4   5   6   7   /
    //  
    //  8   9   10  11  \
    //                    = boardState[1]
    //  12  13  14  15  /
    //  
    //  16  17  18  19  \
    //                    = boardState[2]
    //  20  21  22  23  /
    //  
    //  24  25  26  27  \
    //                    = boardState[3]
    //  28  29  30  31  /
    //
    //  DOWN SIDE = P1
    //  Each int represents 2 rows, bits 0-2 for upleft             (ex. 0)
    //                              bits 3-5 for upleft + right 1   (ex. 1)
    //                              bits 21-23 for downright        (ex. 7)
    //
    //  Each valid move is expressed as an array of length maxJumps+2.
    //  [ startPos , endPos , jump0 , jump1, ... , jumpmaxJumps ]
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

    // All getSquare func return 3 bit value at square relative to given square
    // If the desired square DNE, return -1
    // Direction of desired square based on the function (Up, UpAdj, Down, DownAdj)

    // getSquareUp/Down are simple getSquare same column, row above/below.
    private int getSquareUp( int squareNum ) {
        return (squareNum>3) ? squareNum-4 : -1;
    }
        //if( ((squareNum/4)&1) == 0 ) {    // Even row
        //    if(squareNum/4 == 0 )
        //        return -1;
            //return (boardState[squareNum/4-1]&(7 << ((squareNum&3)+4)*3)) >> ((squareNum&3)+4)*3;
        //}
        // Odd row
        //return (boardState[squareNum/4]&(7 << (squareNum&3)*3)) >> (squareNum&3)*3;
    
    private int getSquareDown( int squareNum ) {
        if( ((squareNum/4)&1) == 1 ) {   // Odd row
            if(squareNum/4 == 7 )
                return -1;
            return (boardState[squareNum/4+1]&(7 << (squareNum&3)*3)) >> (squareNum&3)*3;
        }
        // Even row
        return (boardState[squareNum/4]&(7 << ((squareNum&3)+4)*3)) >> ((squareNum&3)+4)*3;
    }


    // getSquareAdj are more tricky since they must also check if the column exists too.
    // The column to look at changes based on row even vs odd
    private int getSquareUpAdj( int squareNum ) {
        if( ((squareNum/4)&1) == 0 ) {    // Even row, look right 1 column
            if( (squareNum&3) == 3 )      // Right most column
                return -1;
            if( squareNum/4 == 0 )
                return -1;
            return (boardState[squareNum/4-1]&(7 << ((squareNum&3)+5)*3)) >> ((squareNum&3)+5)*3;
        }
        // Odd row, look left 1 column
        if( (squareNum&3) == 0 )          // Left most column
            return -1;
        return (boardState[squareNum/4]&(7 << ((squareNum&3)-1)*3)) >> ((squareNum&3)-1)*3;
    }

    private int getSquareDownAdj( int squareNum ) {
        if( ((squareNum/4)&1) == 1 ) {    // Odd row, look left 1 column
            if( (squareNum&3) == 0 )
                return -1;
            if( squareNum/4 == 7 )
                return -1;
            return (boardState[squareNum/4+1]&(7 << ((squareNum&3)-1)*3)) >> ((squareNum&3)-1)*3;
        }
        if( (squareNum&3) == 3)
            return -1;
        return (boardState[squareNum/4]&(7 << ((squareNum&3)+5)*3)) >> ((squareNum&3)+5)*3;
    }

    public int[][] getValidMovesP0() {
        int currSqVal;
        for( int sq = 0; sq < 32; sq++ ) {
            currSqVal = getSquare(sq);
            if( (currSqVal&3) == 1 ) {      // If occupied and Player0's piece
                if( currSqVal == 7 ) {      // If king
                }
                else {                      // Not a king
                    if( (getSquareDown(sq)&1) == 0 ) {    //Empty
                        validMovesP0[numValidMovesP0][0] = sq;
                        validMovesP0[numValidMovesP0][1] = 
                    getSquareDownAdj(sq);







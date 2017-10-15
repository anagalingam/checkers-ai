class board {
    
    // Each of 32 squares is at maximum a 3 bit value:
    // 0    Empty
    // 1    P1's Normal Piece
    // 2    P0's Normal Piece
    // 3    P1's King
    // 4    P0's King
    //
    //  Transformation of game board from
    //
    //      28      29      30      31
    //  24      25      26      27
    //      20      21      22      23
    //  16      17      18      19      
    //      12      13      14      15
    //  8       9       10      11
    //      4       5       6       7
    //  0       1       2       3
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
    private int[][][] validMoves;
    private int[] numValidMoves = {0 , 0};
  
    // Zero parameter constructor creates a "new game" board
    public board() {
        // Int Array
        // 7190235 => Not King, Player 1, Occupied all 4 cols, 2 rows
        boardState = { 4793490, 1170 , 2396160, 2396745 };
        validMovesP0 = new int[2][maxValidMoves][maxJumps+2];
        validMovesP1 = new int[2][maxValidMoves][maxJumps+2];
        
        // Char Array
        // 1755 => Not King, Player 1, Occupied all 4 columns
        // 585  => Not King, Player 0, Occupied all 4 columns
        //boardState = {1755, 1755, 1755, 0, 0, 585, 585, 585};
    }
    
    // inputBoard must be checked by caller
    public board(int[] inputBoard) {
        boardState = inputBoard;
        validMovesP0 = new int[2][maxValidMoves][maxJumps+2];
        validMovesP1 = new int[2][maxValidMoves][maxJumps+2];
    }
    
    // Return 3 bit value at specified square
    private int getSquareVal( int squareNum ) {
        return squareNum == -1 ? -1 : (boardState[squareNum/8]&(7 << (squareNum&7)*3)) >> (squareNum&7)*3;
    }

    // If the desired square DNE, return -1
    // Directions: Up = 0, Up2 = 1, Down = 2, Down2 = 3

    private int getSquareDir( int squareNum, int dir ) {
        if( dir == 0 )
            return (squareNum < 28) ? squareNum+4 : -1;
        if( dir == 1 ) {
            if( ((squareNum/4)&1) == 1 )    // Odd
                return (squareNum > 27 || ((squareNum&3) == 3)) ? -1 : squareNum+5;
            return ((squareNum&3) == 0) ? -1 : squareNum+3; // Even
        }
        if( dir == 2 )
            return (squareNum>3) ? squareNum-4 : -1;
        
        if( ((squareNum/4)&1) == 0 )    // Even
            return (squareNum < 4 || ((squareNum&3) == 0)) ? -1 : squareNum-5;
        return ((squareNum&3)==0) ? -1 : squareNum-3;   // Odd
    }   
    

    public void updateValidMoves(int player) {
        int sqVal;
        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                recursiveMoveFinder( sq, sq, 0, player, (sqVal > 2 ? true : false))
        }
    }

    private void recursiveMoveFinder( int startSq, int currentSq, int numJumpsSoFar , int player , boolean king ) {
        int tmpSq, startDir, stopDir, currentSqVal, endSqVal, jumpedSqVal;
        if( king ) {
            startDir = 0;
            stopDir = 4;
        }
        else if( player == 0) {
            startDir = 0;
            stopDir = 2;
        }
        else {
            startDir = 2;
            stopDir = 4;
        }
        for( int dir = 0; dir < 2; dir++ ) {
            tmpSq = getSquareDir(currentSq,dir);
            if( getSquareVal(tmpSq) == 0 && startSq == currentSq) {
                validMoves[player][numValidMoves[player]][0] = startSq;
                validMoves[player][numValidMoves[player]][1] = tmpSq;
                numValidMoves[player]++;
            }
            else if( (getSquareVal(tmpSq)%2) == (player == 0 ? 1 : 0) && getSquareVal(getSquareDir(tmpSq,dir)) == 0) {
                validMoves[player][numValidMoves[player]][0] = startSq;
                validMoves[player][numValidMoves[player]][1] = getSquareDir(tmpSq,dir);
                for(int ii = numJumpsSoFar; ii > 0; ii--)
                    validMoves[player][numValidMoves[player]][2+ii] = validMoves[player][numValidMoves[player]-1][2+ii];
                validMoves[player][numValidMoves[player]][2+numJumpsSoFar] = tmpSq;
                numValidMoves[player]++;

                currentSqVal = getSquareVal(currentSq);
                endSqVal = getSquareVal(getSquareDir(tmpSq,dir));
                jumpedSqVal = getSquareVal(tmpSq);

                applySingleJump( currentSq, currentSqVal, getSquareDir(tmpSq,dir), endSqVal, tmpSq, jumpedSqVal);
                
                recursiveMoveFinder( startSq, getSquareDir(tmpSq,dir), numJumpsSoFar+1 , player , king );
                
                removeSingleJump( currentSq, currentSqVal, getSquareDir(tmpSq,dir), endSqVal, tmpSq, jumpedSqVal);
            }
        }
    }

    private void applySingleJump( int startSq, int startSqVal, int endSq, int endSqVal, int jumpedSq, int jumpedSqVal ) {
        boardState[startSq/8] -= startSqVal << (startSq&7)*3;
        boardState[endSq/8] += endSqVal << (endSq&7)*3;
        boardState[jumpedSq/8] -= jumpedSqVal << (jumpedSq&7)*3;
    }
    private void removeSingleJump( int startSq, int endSq, int jumpedSq, int startSqVal, int endSqVal, int jumpedSqVal ) {
        boardState[startSq/8] += startSqVal << (startSq&7)*3;
        boardState[endSq/8] -= endSqVal << (endSq&7)*3;
        boardState[jumpedSq/8] += jumpedSqVal << (jumpedSq&7)*3;
    }

    

import java.util.Scanner;
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
    private final static int maxValidMoves = 500;  // Overestimating.
    private final static int maxJumps = 9;          // Looked up online
    private final static int moveLen = maxJumps+3;  // First 2 indices are startSq, endSq, last index is heuristic of move
    private int[][] validMoves;
    public int numValidMoves = 0;
    private boolean mustJump = false;
    private int[] currMoveSqVals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; 

    public static final int posINF = 2000000000;
    public static final int negINF = -2000000000;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static final int[] worstMaxMove = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, negINF};
    public static final int[] worstMinMove = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, posINF};

  
    // Zero parameter constructor creates a "new game" board
    public board() {
        // Int Array
        boardState = new int[]{ 4793490, 1170 , 2396160, 2396745 };
        validMoves = new int[maxValidMoves][moveLen];
    }
    
    // inputBoard must be checked by caller
    public board(int[] inputBoard) {
        boardState = inputBoard;
        validMoves = new int[maxValidMoves][moveLen];
    }

    public void printBoard() {
        int currVal;
        for( int row = 7; row >= 0; row-- ) {
            for( int reps = 0; reps < 3; reps++) {
                if( reps == 1 )
                    System.out.print(Integer.toString(row+1)+ "  ");
                else
                    System.out.print("   ");
                for( int col = 0; col < 8; col++ ) {
                    if( row%2 == col%2 ) {
                        if( reps == 1 ) {
                            currVal = getSquareVal(row*4+col/2);
                            if( currVal == 0 )
                                System.out.print(ANSI_RED_BACKGROUND + "     " + ANSI_RESET);
                            else if( currVal == 1 )
                                System.out.print(ANSI_RED_BACKGROUND + ANSI_BLUE + "  O  " + ANSI_RESET);
                            else if( currVal == 2)
                                System.out.print(ANSI_RED_BACKGROUND + "  O  " + ANSI_RESET);
                            else if( currVal == 3)
                                System.out.print(ANSI_RED_BACKGROUND + ANSI_BLUE + "  K  " + ANSI_RESET);
                            else
                                System.out.print(ANSI_RED_BACKGROUND + "  K  " + ANSI_RESET);
                        }
                        else
                            System.out.print(ANSI_RED_BACKGROUND + "     "+ ANSI_RESET);
                    }
                    else
                        System.out.print("     ");
                    if( col == 7 )
                        System.out.print('\n');
                }
            }
        }
        System.out.println("     A    B    C    D    E    F    G    H");
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
        return ((squareNum&3)==3) ? -1 : squareNum-3;   // Odd
    }   
    

    public boolean updateValidMoves(int player) {
        mustJump = false;
        while( numValidMoves > 0 ) {
            for(int ii = 0; ii < moveLen; ii++ ) 
                validMoves[numValidMoves-1][ii] = 0;
            numValidMoves--;
        }
        int sqVal;
        int anyMove = 0;
        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                anyMove += findAnyMove( sq, player, sqVal > 2 ? true : false );
        }
        if( anyMove == 0 )
            return false;

        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                recursiveMoveFinder( sq, sq, 0, player, (sqVal > 2 ? true : false));
        }
        return true;
    }

    public String ind2str( int sq ) {
        String row = Integer.toString(sq/4+1);
        String col = null;
        if( (sq/4)%2 == 0 ) {
            if( sq%4 == 0 )
                col = new String("A");
            else if( sq%4 == 1 )
                col = new String("C");
            else if( sq%4 == 2 )
                col = new String("E");
            else
                col = new String("G");
        }
        else {
            if( sq%4 == 0 )
                col = new String("B");
            else if( sq%4 == 1 )
                col = new String("D");
            else if( sq%4 == 2 )
                col = new String("F");
            else
                col = new String("H");
        }
        return col.concat(row);
    }

    public void printValidMoves(int player) {
        System.out.print("Printing " + Integer.toString(numValidMoves) + " valid moves for player ");
        if(player == 1)
            System.out.println("1");
        else
            System.out.println("2");
        for( int ii = 0; ii < numValidMoves; ii++) {
            for( int jj = 0; jj < maxJumps+2; jj++) {
                if(jj == 0) {
                    System.out.print("Move#" + Integer.toString(ii+1) + "\t \t Start: ");
                    System.out.print(ind2str(validMoves[ii][jj]));
                }
                else if(jj == 1) {
                    System.out.print("End: ");
                    System.out.print(ind2str(validMoves[ii][jj])+ '\t');
                }
                else if(jj == 2 && validMoves[ii][2] != 0) {
                    System.out.print("Jumped: ");
                    System.out.print(ind2str(validMoves[ii][jj]));
                }
                else if (validMoves[ii][jj] == 0)
                    break;
                else 
                    System.out.print(ind2str(validMoves[ii][jj]));
                System.out.print('\t');
            }
            System.out.println(' ');
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
        for( int dir = startDir; dir < stopDir; dir++ ) {
            tmpSq = getSquareDir(currentSq,dir);
            if( !mustJump) {
                if( getSquareVal(tmpSq) == 0 ) {
                    validMoves[numValidMoves][0] = startSq;
                    validMoves[numValidMoves][1] = tmpSq;
                    // Below ensures non-lazy deletion in move Stack
                    for(int ii = 2; ii < moveLen-1; ii++)
                        validMoves[numValidMoves][ii] = 0;
                    validMoves[numValidMoves][moveLen-1] = Integer.MIN_VALUE;
                    System.out.print("Move# " + Integer.toString(numValidMoves));
                    printArray(validMoves[numValidMoves]);
                    numValidMoves++;
                }
            }
            else if( getSquareVal(tmpSq) != 0 && (getSquareVal(tmpSq)%2) == (player == 0 ? 1 : 0) && getSquareVal(getSquareDir(tmpSq,dir^1)) == 0) {
                validMoves[numValidMoves][0] = startSq;
                validMoves[numValidMoves][1] = getSquareDir(tmpSq,dir^1);
                for(int ii = 0; ii < numJumpsSoFar; ii++)
                    validMoves[numValidMoves][2+ii] = validMoves[numValidMoves-1][2+ii];
                validMoves[numValidMoves][2+numJumpsSoFar] = tmpSq;
                // Below ensures non-lazy deletion in move Stack
                for( int ii = 3+numJumpsSoFar; ii < moveLen; ii++)
                    validMoves[numValidMoves][ii] = 0;
                validMoves[numValidMoves][moveLen-1] = Integer.MIN_VALUE;
                numValidMoves++;
                System.out.print("Move# " + Integer.toString(numValidMoves-1));
                printArray(validMoves[numValidMoves-1]);

                currentSqVal = getSquareVal(currentSq);
                jumpedSqVal = getSquareVal(tmpSq);

                applySingleJump( currentSq, getSquareDir(tmpSq,dir^1), tmpSq, currentSqVal, jumpedSqVal);
                
                recursiveMoveFinder( startSq, getSquareDir(tmpSq,dir^1), numJumpsSoFar+1 , player , king );
                
                removeSingleJump( currentSq, getSquareDir(tmpSq,dir^1), tmpSq, currentSqVal, jumpedSqVal);
            }
        }
    }

    private void applySingleJump( int startSq, int endSq, int jumpedSq, int startSqVal, int jumpedSqVal ) {
        boardState[startSq/8] -= startSqVal << (startSq&7)*3;
        boardState[endSq/8] += startSqVal << (endSq&7)*3;
        boardState[jumpedSq/8] -= jumpedSqVal << (jumpedSq&7)*3;
    }
    private void removeSingleJump( int startSq, int endSq, int jumpedSq, int startSqVal, int jumpedSqVal ) {
        boardState[startSq/8] += startSqVal << (startSq&7)*3;
        boardState[endSq/8] -= startSqVal << (endSq&7)*3;
        boardState[jumpedSq/8] += jumpedSqVal << (jumpedSq&7)*3;
    }

    private boolean kingMe( int player , int endSq ) {
        if( player == 0 )
            return (endSq/4 == 7) ? true : false;
        return (endSq/4 == 0) ? true: false;
    }

    public void applySingleMove( int player, int moveNumber ) {
        int[] move = validMoves[moveNumber];
        //System.out.print("Applying move ");
        //printArray(move);
        int startSqVal = getSquareVal(move[0]);
        int endSqVal = kingMe(player,move[1]) && startSqVal<3 ? startSqVal+2 : startSqVal;
        for( int ii = 0; ii < moveLen-1; ii++ ) {
            if( ii == 0 )
                boardState[move[ii]/8] -= startSqVal << (move[ii]&7)*3;
            else if( ii == 1 )
                boardState[move[ii]/8] += endSqVal << (move[ii]&7)*3;
            else if( move[ii] == 0 )
                break;
            else
                boardState[move[ii]/8] -= getSquareVal(move[ii]) << (move[ii]&7)*3;
        }
    }
    // Same function, but passed a move, not a moveNumber on the validMoves stack
    // Keep two functions to avoid two function calls for the version with just moveNumber
    // since that version is used most in the alphabeta search
    public void applySingleMove( int player, int move[] ) {
        int startSqVal = getSquareVal(move[0]);
        int endSqVal = kingMe(player,move[1]) && startSqVal<3 ? startSqVal+2 : startSqVal;
        for( int ii = 0; ii < moveLen-1; ii++ ) {
            if( ii == 0 )
                boardState[move[ii]/8] -= startSqVal << (move[ii]&7)*3;
            else if( ii == 1 )
                boardState[move[ii]/8] += endSqVal << (move[ii]&7)*3;
            else if( move[ii] == 0 )
                break;
            else
                boardState[move[ii]/8] -= getSquareVal(move[ii]) << (move[ii]&7)*3;
        }
    }       

    public void revertSingleMove( int[] sqVals, int moveNum) {
        int[] move = validMoves[moveNum];
        boardState[move[0]/8] += sqVals[0] << (move[0]&7)*3;
        boardState[move[1]/8] -= sqVals[0] << (move[1]&7)*3;
        for(int ii = 2; ii < maxJumps+2; ii++ ) {
            if(move[ii] == 0)
                break;
            boardState[move[ii]/8] += sqVals[ii] << (move[ii]&7)*3;
        }
    } 
    
    public int heuristic(int player, boolean isMaxPlayer) {
        int res, sqVal;
        res = 0;
        for( int sq = 0; sq < 32; sq++) {
            sqVal = getSquareVal(sq);
            if( sqVal == 0 )
                continue;
            if( sqVal % 2 == player ) {
                if( sqVal > 2 )
                    res += 15;
                else
                    res += 10;
            }
            else {
                if( sqVal > 2 )
                    res -= 15;
                else
                    res -= 10;
            }
        }
        return isMaxPlayer ? res : -1*res;
    }

    public boolean aiMove( int player, long time ) {    // Returns TRUE if move was performed, FALSE if AI has no moves.
        mustJump = false;
        for( int p = 0; p < 2; p++ ) {
            while( numValidMoves > 0 ) {
                for(int ii = 0; ii < moveLen; ii++ ) 
                    validMoves[numValidMoves-1][ii] = 0;
                numValidMoves--;
            }
        }
        int sqVal;
        int anyMove = 0;
        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                anyMove += findAnyMove( sq, player, sqVal > 2 ? true : false );
        }
        if( anyMove == 0 ) {    // If no moves, AI loses.
            return false;
        }

        int depth = 1;
        long timeLim = time-1000000;
        int[] bestMove = new int[moveLen];
        long t0 = System.nanoTime();

        while( true ) {
            System.out.println("Depth is " + depth );
            numValidMoves = 1;      // Bottom of Stack is reserved for best heuristic value
            validMoves[0][moveLen-1] = Integer.MIN_VALUE;
            alphaBeta( depth, negINF, posINF, player, true, 0, timeLim+t0);
            if( validMoves[0][moveLen-1] == Integer.MIN_VALUE )
                break;
            printArray(validMoves[0]);
            for( int move = 1; move < numValidMoves; move++ ) {
                System.out.print("Move#" + move );
                printArray(validMoves[move]);
                if( validMoves[move][moveLen-1] == validMoves[0][moveLen-1] ) {
                    for( int ii = 0; ii < moveLen; ii++ )
                        bestMove[ii] = validMoves[move][ii];
                    break;
                }
            }
            depth++;
            if( depth > 1 )
                break;
        }
        applySingleMove(player, bestMove);
        return true;
    }
    // appliedMoveNum is the move (ply of the gametree) that got me to the current boardState
    // alphaBeta must assign its return value to the appliedMoveNum that created it.
    public void alphaBeta( int depth, int alpha, int beta, int player, boolean isMaxPlayer, int appliedMoveNum, long timeLim) {
        mustJump = false;

        if( System.nanoTime() > timeLim )
            return;
        if( depth == 0 ) {
            validMoves[appliedMoveNum][moveLen-1] = heuristic(player, isMaxPlayer);
            return;
        }
        int sqVal;

        // Below for loop checks for terminal node and sets mustJump flag if necessary.
        int anyMove = 0;
        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                anyMove += findAnyMove( sq, player, sqVal > 2 ? true : false );
        }
        if( anyMove == 0 ) {    // If no moves, you lose. Return worst heuristic for this player.
            validMoves[appliedMoveNum][moveLen-1] = isMaxPlayer ? negINF : posINF;
            return;
        }
        // Below for loop finds all moves for the current boardState

        int firstValidMove = numValidMoves;
        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                recursiveMoveFinder( sq, sq, 0, player, (sqVal > 2 ? true : false));
        }
        int v;
        if( isMaxPlayer ) {
            v = negINF; 
            for( int moveNum = firstValidMove; moveNum < numValidMoves; moveNum++ ) {
                for( int ii = 0; ii < moveLen-1; ii++ )
                    currMoveSqVals[ii] = getSquareVal(validMoves[moveNum][ii]);
                applySingleMove(player, moveNum);
                alphaBeta( depth-1, alpha, beta, (player+1)&1, !isMaxPlayer, moveNum, timeLim );
                revertSingleMove(currMoveSqVals, moveNum);
                v = v > validMoves[moveNum][moveLen-1] ? v : validMoves[moveNum][moveLen-1];
                alpha = alpha > v ? alpha : v;
                if( beta <= alpha )
                    break;
            }
        }
        else {      //Minimizing Player
            v = posINF;
            for( int moveNum = firstValidMove; moveNum < numValidMoves; moveNum++ ) {
                for( int ii = 0; ii < moveLen-1; ii++ )
                    currMoveSqVals[ii] = getSquareVal(validMoves[moveNum][ii]);
                applySingleMove(player,moveNum);
                alphaBeta( depth-1, alpha, beta, (player+1)&1, !isMaxPlayer, moveNum, timeLim);
                revertSingleMove(currMoveSqVals,moveNum);
                v = v < validMoves[moveNum][moveLen-1] ? v : validMoves[moveNum][moveLen-1];
                beta = beta < v ? beta : v;
                if( beta <= alpha )
                    break;
            }
        }
        while( numValidMoves > firstValidMove )
            numValidMoves--;
        validMoves[appliedMoveNum][moveLen-1] = v;
        return;
    }

    private int findAnyMove( int startSq, int player, boolean king ) {
        int tmpSq, startDir, stopDir;
        int res = 0;
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
        for( int dir = startDir; dir < stopDir; dir++ ) {
            tmpSq = getSquareDir(startSq,dir);
            if( getSquareVal(tmpSq) == 0 ) {
                res = 1;
            }
            else if( getSquareVal(tmpSq) != 0 && (getSquareVal(tmpSq)%2) == (player == 0 ? 1 : 0) && getSquareVal(getSquareDir(tmpSq,dir^1)) == 0) {
                mustJump = true;
                res = 1;
                return res;
            }
        }
        return res;
    }

    public boolean play(int turn, Scanner sc, boolean[] isAI, long timeLim ) {
        if( isAI[turn%2] ) {
            if( aiMove( turn%2, timeLim ) )
                return true;
            else {
                System.out.println("Player " + Integer.toString(turn%2) + " has no more moves. Player " + Integer.toString((turn+1)%2) + " WINS!");
                return false;
            }
        }
        int inputMove = 0;
        boolean firstTry = true;
        printBoard();
        if(!updateValidMoves(turn%2)) {
            System.out.println("Player " + Integer.toString(turn%2) + " has no more moves. Player " + Integer.toString((turn+1)%2) + " WINS!");
            return false;
        }
        printValidMoves(turn%2);
        while( inputMove<1 || inputMove>numValidMoves) {
            try{
                if(firstTry) {
                    System.out.println("Enter a move number from 1 - " + Integer.toString(numValidMoves));
                    firstTry = false;
                }
                else {
                    System.out.println("Invalid move. Enter a move number from 1 - " + Integer.toString(numValidMoves));
                }
                inputMove = Integer.parseInt(sc.nextLine());
            }
            catch (Exception exp) {
                inputMove = 0;
            }
        }
        applySingleMove(turn%2, inputMove-1);
        return true;
    }

    public static void printArray(int[] arr) {
        System.out.print("[ ");
        for( int ii = 0; ii < arr.length; ii++ )
            System.out.print( arr[ii] + " " );
        System.out.println("]");
    }
}


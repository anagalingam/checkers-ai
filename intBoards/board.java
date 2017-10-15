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
    private final static int maxValidMoves = 1000;  // Overestimating. Each piece would have on avg 41.667 valid moves
    private final static int maxJumps = 9;          // Looked up online
    private int[][][] validMoves;
    public int[] numValidMoves = {0 , 0};
  
    // Zero parameter constructor creates a "new game" board
    public board() {
        // Int Array
        boardState = new int[]{ 4793490, 1170 , 2396160, 2396745 };
        validMoves = new int[2][maxValidMoves][maxJumps+2];
        
        // Char Array
        // 1755 => Not King, Player 1, Occupied all 4 columns
        // 585  => Not King, Player 0, Occupied all 4 columns
        //boardState = {1755, 1755, 1755, 0, 0, 585, 585, 585};
    }
    
    // inputBoard must be checked by caller
    public board(int[] inputBoard) {
        boardState = inputBoard;
        validMoves = new int[2][maxValidMoves][maxJumps+2];
    }

    public void printBoard() {
        for( int row = 7; row >= 0; row-- ) {
            for( int col = 0; col < 8; col++ ) {
                if( row%2 == col%2 ) {
                    System.out.print(getSquareVal(row*4+col/2));
                    System.out.print('\t');
                }
                else
                    System.out.print('\t');
                if( col == 7 )
                    System.out.println('\n');
            }
        }
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
        while( numValidMoves[player] > 0 ) {
            for(int ii = 0; ii < maxJumps+2; ii++ ) 
                validMoves[player][numValidMoves[player]][ii] = 0;
            numValidMoves[player]--;
        }
        int sqVal;
        for( int sq = 0; sq < 32; sq++ ) {
            sqVal = getSquareVal(sq);
            if( sqVal > 0 && (sqVal&1) == player )
                recursiveMoveFinder( sq, sq, 0, player, (sqVal > 2 ? true : false));
        }
    }

    public void printValidMoves(int player) {
        System.out.println("Printing " + numValidMoves[player] + " valid moves for player " + player);
        for( int ii = 0; ii < numValidMoves[player]; ii++) {
            for( int jj = 0; jj < maxJumps+2; jj++) {
                if(jj == 0)
                    System.out.print("Move #" + ii + "\t Start: ");
                if(jj == 1)
                    System.out.print("End: ");
                if(jj == 2 && validMoves[player][ii][2] != 0)
                    System.out.print("Jumped: ");
                if(validMoves[player][ii][jj] == 0)
                    break;
                System.out.print(validMoves[player][ii][jj]);
                System.out.print('\t');
            }
            System.out.println(' ');
        }
    }

    private static int otherDir(int dir) {
        if( dir == 0 )

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
                jumpedSqVal = getSquareVal(tmpSq);

                applySingleJump( currentSq, getSquareDir(tmpSq,dir), tmpSq, currentSqVal, jumpedSqVal);
                
                recursiveMoveFinder( startSq, getSquareDir(tmpSq,dir), numJumpsSoFar+1 , player , king );
                
                removeSingleJump( currentSq, getSquareDir(tmpSq,dir), tmpSq, currentSqVal, jumpedSqVal);
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

    public void applySingleMove( int player, int moveNumber ) {
        int[] move = validMoves[player][moveNumber];
        System.out.print("Applying move ");
        printArray(move);
        int startSqVal = getSquareVal(move[0]);
        for( int ii = 0; ii < maxJumps+2; ii++ ) {
            if( ii == 0 )
                boardState[move[ii]/8] -= startSqVal << (move[ii]&7)*3;
            else if( ii == 1 )
                boardState[move[ii]/8] += startSqVal << (move[ii]&7)*3;
            else if( move[ii] == 0 )
                break;
            else
                boardState[move[ii]/8] -= getSquareVal(move[ii]) << (move[ii]&7)*3;
        }
    }

    public static void printArray(int[] arr) {
        System.out.print("[ ");
        for( int ii = 0; ii < arr.length; ii++ ) {
            System.out.print( arr[ii] + " " );
        }
        System.out.println("]");
    }


    public static void main( String[] args ) {
        board game1 = new board();
        int turn = 0;
        Scanner sc = new Scanner(System.in);
        while(true) {
            game1.printBoard();
            game1.updateValidMoves(turn%2);
            game1.printValidMoves(turn%2);
            System.out.println("Enter a move number from 0 - " + game1.numValidMoves[turn%2]);
            game1.applySingleMove(turn%2,sc.nextInt());
            turn++;
        }
    }
}

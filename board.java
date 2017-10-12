 

class board {
    
    private final byte maxRow = 8;
    private final byte maxCol = 4;
    // A board is stored as a byte[32]
    // Visualization of the board:
    //
    //           player 2
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
    //           player 1
    //
    // Byte Values:
    // 0    empty
    // 1    regular piece player 1
    // 2    regular piece player 2
    // 3    king player 1
    // 4    king player 2

    private byte[][] boardState;
    
    class validMoveStack {
        int size;
        validMove first;
    
        class validMove {
            byte currentPos;
            byte nextPos;
            jumpedPos ;
            validMove next;

            class jumpList {
                byte pos;
                jumpedPos nextJump;

                public jumpList( byte piece ) {
                    pos = piece;
                    nextJump = null;
                }

            validMove( byte start, byte end, byte jumped, validMove n) {
                currentPos = start;
                nextPos = end;
                jumpedPos = jumped;
                next = n;
            }
        }
        
        validMoveStack() {
            size = 0;
            first = null;
        }

        void push(byte start, byte end, byte jumped) {
            first = new validMove( start , end , jumped , first );
            size++;
        }

        public byte[] pop() {
            byte[] move = new byte[3];
            move[0] = first.currentPos;
            move[1] = first.nextPos;
            move[2] = first.jumpedPos;
            first = first.next;
            size--;
            return validMove;
        }
   
    // Zero parameter constructor creates a "new game" board
    public board() {
        boardState = new byte[maxRow][maxCol];
        for( byte row = 0; row < maxRow; row++ )
            if(row < 3)
                for( byte col = 0; col < maxCol; col++ )
                    boardState[row][col] = 2;
            else if(row > 4)
                for( byte col = 0; col < maxCol; col++ )
                    boardState[row][col] = 1;
    }
    
    // inputBoard must be checked by caller
    public board(byte[][] inputBoard) {
        boardState = inputBoard;
    }
    
    public static byte rc2index( byte row , byte col ) {
        return row*4+col;
    public validMoveStack getValidMoves( boolean player )
        validMoveStack moves = new validMoveStack();
        for( byte row = 0; row < maxRow; row++ ) {
            for( byte col = 0; col < maxCol; col++ ) {
                // Player 1
                if( player ) {
                    // If King
                    if(boardState[row][col] == 3) {
                        // Moves backwards
                        if( row+1 < maxRow ) {
                            if( boardState[row+1][col] == 0 )
                                moves.push( rc2index(row,col) , rc2index(row+1,col) , -1 );
                            else if( (boardState[row+1][col]%2 == 0) && (row+2 < maxRow) && (col+1 < maxCol) && (boardState[row+2][col+1] == 0) )
                                moves.push( rc2index(row,col) , rc2index(row+2,col+1 ), rc2index(row+1,col) );
                            if( col - 1 > 0 ) {
                                if( boardState[row+1][col-1] == 0 )
                                    moves.push( rc2index(row,col), rc2index(row+1,col-1), -1);
                                else if( (boardState[row+1][col-1]%2 == 0) && (row+2 < maxRow) && (col-2 > 0) && (boardState[row+2][col-2] == 0) )
                                    moves.push( rc2index(row,col) , rc2index(row+2,col-2) , rc2index(row+1,col-1) );
                            }
                        }
                        // Moves forwards
                        if( row - 1 < maxRow ) { 
                            if( boardState[row-1][col] == 0 )
                                moves.push( rc2index(row,col) , rc2index(row-1,col) , -1);
                            else if( (boardState[

        

class validMoveStack {
    int size = 0;
    validMove first = null;
    boolean mustJump = false;       // If any valid move can jump, then set true
    
    class validMove {
        byte currentPos;
        byte nextPos;
        jumpNode jumpedPos;     // A linked list of jumped pieces
        byte jumps;
        validMove nextMove;

        class jumpNode {
            byte pos;
            jumpNode nextJump;

            jumpNode( byte piece ) {
                pos = piece;
            }

            void add( byte jump ) {
                jumpNode tmp = this;
                while( tmp.nextJump != null )
                    tmp = tmp.nextJump;
                tmp.nextJump = new jumpNode( jump );
            }
        }

        validMove( byte start, byte end, validMove n ) {
            currentPos = start;
            nextPos = end;
            nextMove = n;
        }

        validMove( byte start, byte end, byte jumped, validMove n) {
            currentPos = start;
            nextPos = end;
            jumpedPos = jumped;
            nextMove = n;
        }
    }
        
    validMoveStack() {}

    void addJumpToFirst( byte newEnd , byte jumped ) {
        first.jumpedPos.add( jumped );
        first.nextPos = newEnd;
    }

    void push( byte start, byte end ) {
        if( !mustJump ) {
            first = new validMove( start , end , first );
            size++;
        }
    }

    void push( byte start, byte end, byte jumped ) {
        first = new validMove( start , end , jumped , first );
        size++;
    }

    public byte[] pop() {
        byte[] move = new byte[3];
        move[0] = first.currentPos;
        move[1] = first.nextPos;
        move[2] = first.jumpedPos;
        first = first.nextMove;
        size--;
        return validMove;
    }
}

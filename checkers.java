import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

class checkers {
    public static final long SEC2NANO = 1000000000; 
    
    
    public static void main( String[] args ) {
        int turn = 0;
        boolean[] isAI = new boolean[2];
        long timeLim = 0;
        int[] startBoard = {0 , 0 , 0 , 0};
        String gameType, loadBoard, boardFileName, boardFileLine, time, firstPlayer;
        File boardFile;
        BufferedReader boardFileReader;
        board game = null;
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Arvind's console checkers! Please input a number (1-3) for your game type from the list below:");
        System.out.println("1 \t Player vs. AI");
        System.out.println("2 \t AI vs. AI");
        System.out.println("3 \t Player vs. Player");
        gameType = sc.nextLine();
        while(!(gameType.equals("1") || gameType.equals("2") || gameType.equals("3"))) {
            System.out.println("Invalid input! Please input a number 1, 2, or 3.");
            gameType = sc.nextLine();
        }
        if( Integer.parseInt(gameType) == 1 ) {
            isAI[0] = false;
            isAI[1] = true;
        }
        else if( Integer.parseInt(gameType) == 2 ) {
            isAI[0] = true;
            isAI[1] = true;
        }
        else {
            isAI[0] = false;
            isAI[1] = false;
        }
        System.out.println("Would you like to load a board? (y/n)");
        loadBoard = sc.nextLine();
        while(!(loadBoard.equals("y") || loadBoard.equals("n"))) {
            System.out.println("Invalid input! Please input 'y' or 'n'");
            loadBoard = sc.nextLine();
        }
        if(loadBoard.equals("y")) {
            System.out.print("Please input the name of the board file: ");
            boardFileName = sc.nextLine();
            while(game == null) {
                try{
                    boardFile = new File(boardFileName);
                    boardFileReader = new BufferedReader(new FileReader(boardFile));
                    for( int row = 0; row < 10; row++) {
                        if((boardFileLine = boardFileReader.readLine()) == null)
                            break;
                        if( row == 8 )
                            turn = Integer.parseInt(boardFileLine);
                        else if( row == 9)
                            timeLim = Long.parseLong(boardFileLine)*SEC2NANO;
                        else {
                            boardFileLine = boardFileLine.trim();
                            String[] tokens = boardFileLine.split("\\s+");
                            for( int col = 0; col < tokens.length; col++)
                                startBoard[(7-row)/2] += Integer.parseInt(tokens[col])<< (((7-row)%2)*4+col)*3;
                        }
                    }
                    if( (turn == 1 || turn == 2 ) && timeLim > 0 )
                        game = new board(startBoard);
                }
                catch(Exception exp) {
                }
                if( game == null ) {
                    System.out.println("Invalid board file!");
                    System.out.print("Please input the name of the board file: ");
                    boardFileName = sc.nextLine();
                }
            }
        }
        else {
            game = new board();
            if( gameType.equals("1") || gameType.equals("2")) {
                System.out.println("Enter a time limit for the AI in seconds: ");
                time = sc.nextLine();
                while(timeLim == 0) {
                    try{
                        timeLim = Long.parseLong(time)*SEC2NANO;
                    }
                    catch(Exception exp) {
                        System.out.println("Invalid time! Please enter a time in seconds: ");
                        timeLim = 0;
                        time = sc.nextLine();
                    }
                    if( timeLim < 0 ) {
                        System.out.println("Invalid time! Please enter a time in seconds: ");
                        timeLim = 0;
                        time = sc.nextLine();
                    }
                }
            }
            if( gameType.equals("1") ) 
                System.out.println("NOTE: In AI vs. Player, AI is always player 1 and Player is always player 2.");
            System.out.println("Which player should go first? (1 or 2)");
            firstPlayer = sc.nextLine();
            while(!(firstPlayer.equals("1") || firstPlayer.equals("2"))) {
                System.out.println("Invalid input! Please input '1' or '2'");
                firstPlayer = sc.nextLine();
            }
            turn = Integer.parseInt(firstPlayer)%2;
        }
        while(game.play(turn,sc,isAI, timeLim))
            turn++;
    }
}

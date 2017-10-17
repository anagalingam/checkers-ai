import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;

class checkers {

    public static void main( String[] args ) {
        int turn = 0;
        String loadBoard, boardFileName, boardFileLine;
        File boardFile;
        BufferedReader boardFileReader;
        board game;
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Arvind's console checkers! Would you like to load a board? (y/n)");
        loadBoard = sc.nextLine();
        while(!(loadBoard.equals("y") || loadBoard.equals("n"))) {
            System.out.println("Invalid input. Please input 'y' or 'n'");
            loadBoard = sc.nextLine();
        }
        if(loadBoard.equals("y")) {
            System.out.print("Please input the name of the board file: ");
            boardFileName = sc.nextLine();
            while(game == null) {
                try{
                    boardFile = new File(boardFileName);
                    boardFileReader = new BufferedReader(new FileReader(boardFile));
                    while((boardFileLine = boardFileReader.readLine()) != null ) {






        while( inputMove<1 || inputMove>numValidMoves[turn%2]-firstValidMove) {
            try{
                if(firstTry) {
                    System.out.println("Enter a move number from 1 - " + Integer.toString(numValidMoves[turn%2]-firstValidMove));
                    firstTry = false;
                }
                else {
                    System.out.println("Invalid move. Enter a move number from 1 - " + Integer.toString(numValidMoves[turn%2]-firstValidMove));
                }
                inputMove = Integer.parseInt(sc.nextLine());
            }
            catch (Exception exp) {
                inputMove = 0;
            }
        }
 
        board game = new board();
        while(game.play(turn,sc))
            turn++;
    }
}

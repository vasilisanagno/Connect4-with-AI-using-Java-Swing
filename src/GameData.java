//package ce326.hw3;

public class GameData {
    static final int ROWS = 6;
    static final int COLUMNS = 7;
    char[][] gameArray; //W for empty position R for red(User) and Y for yellow(AI)

    public GameData(){} //1 constructor

    public GameData(char[][] gameArray) { //2 constructor
        this.gameArray=gameArray;
    }

    public char[][] getGameArray() { //returns the game array
        return this.gameArray;
    }

    public void setGameArray(char[][] gameArray) { //sets the game array
        this.gameArray=gameArray;
    }

    public void initializationGameArray() { //initializes the game array with 'W'
        
        for(int i=0; i<ROWS; i++) {
            for(int j=0; j<COLUMNS; j++) {
                this.gameArray[i][j]='W';
            }
        }
    }
    
    public boolean checkColumnFree() { //checks if there is some free column in the game array and returns true if there is or false otherwise

        for(int j=0; j<COLUMNS; j++) {
            if(this.gameArray[0][j]=='W') {
                return true;
            }
        }
        return false;
    }

    public int addInGameArray(String playerTurn,int columnPosition) { //adds a checker in the game array and returns the row of the addition
        int i=0;

        if(playerTurn.equals("AI")) {
            if(this.gameArray[ROWS-1][columnPosition]=='W') {
                this.gameArray[ROWS-1][columnPosition]='Y';
                i=ROWS-1;
            }
            else {
                for(i=1; i<ROWS; i++) {
                    if(this.gameArray[i][columnPosition]!='W') {
                        this.gameArray[i-1][columnPosition]='Y';
                        i=i-1;
                        break;
                    }
                }
            }
        }
        else if(playerTurn.equals("User")) {
            if(this.gameArray[ROWS-1][columnPosition]=='W') {
                this.gameArray[ROWS-1][columnPosition]='R';
                i=ROWS-1;
            }
            else {
                for(i=1; i<ROWS; i++) {
                    if(this.gameArray[i][columnPosition]!='W') {
                        this.gameArray[i-1][columnPosition]='R';
                        i=i-1;
                        break;
                    }
                }
            }
        }
        return i;
    }
    
    public void removeFromGameArray(int rowPosition ,int columnPosition) { //removes the checker from the game array

        this.gameArray[rowPosition][columnPosition]='W';
    }


}
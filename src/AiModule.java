//package ce326.hw3;

import java.util.ArrayList;

public class AiModule {
    int maxDepth; //1 for trivial 3 for medium 4 for hard
    String playerTurn; //AI or User

    public AiModule(){} //1 constructor

    public AiModule(int maxDepth,String playerTurn) { //2 constructor
        this.maxDepth=maxDepth;
        this.playerTurn=playerTurn;
    }

    public int getMaxDepth() { //returns the max depth
        return this.maxDepth;
    }

    public void setMaxDepth(int maxDepth) { //sets the max depth
        this.maxDepth=maxDepth;
    }

    public String getPlayerTurn() { //returns the player that has turn
        return this.playerTurn;
    }

    public void setPlayerTurn(String playerTurn) { //sets the player that has turn
        this.playerTurn=playerTurn;
    }

    public int countCostOrBenefit(char[][] gameArray,int row, int column,int costOrBenefit) { //counts the benefit for AI or User or the cost for AI or User 
        int value=0, times=0;
        char check=0;

        //looks to what must evaluate cost or benefit about AI or User
        if((this.getPlayerTurn().equals("AI")&&costOrBenefit==1)||(this.getPlayerTurn().equals("User")&&costOrBenefit==0)) {
            check='Y';
        }
        else if((this.getPlayerTurn().equals("User")&&costOrBenefit==1)||(this.getPlayerTurn().equals("AI")&&costOrBenefit==0)) {
            check='R';
        }

        //horizontal
        if(column<4) {
            for(int j=column; j<column+4; j++) {
                if(gameArray[row][j]=='W') {
                    continue;
                }
                else {
                    if(gameArray[row][j]==check) {
                        times=0;
                        break;
                    }
                    times++;
                }
            }
            if(times==1) {
                value=value+1;
            }
            if(times==2) {
                value=value+4;
            }
            if(times==3) {
                value=value+16;
            }
            if(times==4) {
                return 10000;
            }
            times=0;
        }
        //vertical
        if(gameArray[row][column]!='W'&&row>GameData.ROWS-4) {
            for(int i=row; i>row-4; i--) {
                if(gameArray[i][column]==check) {
                    times=0;
                    break;
                }
                if(gameArray[i][column]=='W') {
                    break;
                }
                else {
                    times++;
                }
            }
            if(times==1) {
                value=value+1;
            }
            if(times==2) {
                value=value+4;
            }
            if(times==3) {
                value=value+16;
            }
            if(times==4) {
                return 10000;
            }
            times=0;
        }
        //diagonal below
        if(column<GameData.COLUMNS-3&&row<GameData.ROWS-3) {
            for(int i=row,j=column; i<row+4&&j<column+4; i++,j++) {
                if(gameArray[i][j]=='W') {
                    continue;
                }
                else {
                    if(gameArray[i][j]==check) {
                        times=0;
                        break;
                    }
                    times++;
                }
            }
            if(times==1) {
                value=value+1;
            }
            if(times==2) {
                value=value+4;
            }
            if(times==3) {
                value=value+16;
            }
            if(times==4) {
                return 10000;
            }
            times=0;
        }
        //diagonal above
        if(column<GameData.COLUMNS-3&&row>GameData.ROWS-4) {
            for(int i=row,j=column; i>row-4&&j<column+4; i--,j++) {
                if(gameArray[i][j]=='W') {
                    continue;
                }
                else {
                    if(gameArray[i][j]==check) {
                        times=0;
                        break;
                    }
                    times++;
                }
            }
            if(times==1) {
                value=value+1;
            }
            if(times==2) {
                value=value+4;
            }
            if(times==3) {
                value=value+16;
            }
            if(times==4) {
                return 10000;
            }
            times=0;
        } 
        return value;
    }

    public int evaluateMove(GameData gameData) { //evaluates the each move either User or AI
        int cost=0, benefit=0;

        if(this.getPlayerTurn().equals("AI")) { //benefit and cost for AI
            for(int i=0; i<GameData.ROWS; i++) {
                for(int j=0; j<GameData.COLUMNS; j++) {
                    cost=cost+countCostOrBenefit(gameData.getGameArray(), i, j,1); //costOrBnefit=1 if it is cost or 0 if it is benefit
                    benefit=benefit+countCostOrBenefit(gameData.getGameArray(), i, j,0);
                    if(benefit>=10000) { //if it is a win for AI
                        return 10000;
                    }
                }
            }
        }
        else if(getPlayerTurn().equals("User")) { //benefit and cost for User
            for(int i=0; i<GameData.ROWS; i++) {
                for(int j=0; j<GameData.COLUMNS; j++) {
                    cost=cost-countCostOrBenefit(gameData.getGameArray(), i, j,1); //costOrBnefit=1 if it is cost or 0 if it is benefit
                    benefit=benefit-countCostOrBenefit(gameData.getGameArray(), i, j,0);
                    if(benefit<=-10000) { //if it is a win for User
                        return -10000;
                    }
                }
            }
        }
        //sub between benefit and cost either AI or User
        return benefit-cost;
    }

    public ArrayList<Integer> minMaxWithAlphaBeta(GameData gameData,int depth,boolean max,int alpha,int beta) { //minmax algorithm with alpha beta pruning to find the the best column to put the checker the AI

        int score=evaluateMove(gameData);
        if(depth==this.maxDepth||!gameData.checkColumnFree()||score==10000||score==-10000) { //if it is terminal node or is detected some win or the game board is full return
            ArrayList<Integer> evaluation = new ArrayList<>();
            evaluation.add(0,score);
            evaluation.add(1,0);
            return evaluation;
        }

        if(max) { //maximizer node, the AI
            ArrayList<Integer> bestEvaluation = new ArrayList<>();
            bestEvaluation.add(0,-Integer.MAX_VALUE);
            bestEvaluation.add(1,0);
            for(int j=0; j<GameData.COLUMNS; j++) {
                if(gameData.getGameArray()[0][j]=='W') {
                    int rowPosition = gameData.addInGameArray("AI", j);
                    this.setPlayerTurn("AI");

                    ArrayList<Integer> value = minMaxWithAlphaBeta(gameData, depth+1, false, alpha, beta);

                    this.setPlayerTurn("AI");
                    gameData.removeFromGameArray(rowPosition, j);

                    if(bestEvaluation.get(0)<value.get(0)) { //chooses the best column and best score for AI
                        bestEvaluation.add(0,value.get(0));
                        bestEvaluation.add(1,j);    
                    }
                    if(alpha<bestEvaluation.get(0)) {
                        alpha=bestEvaluation.get(0);
                    }
                    if(alpha>=beta) {
                        break;
                    }
                }
            }
            return bestEvaluation;
        }
        else { //minimizer node, the User
            ArrayList<Integer> worstEvaluation = new ArrayList<>();
            worstEvaluation.add(0,Integer.MAX_VALUE);
            worstEvaluation.add(1,0);
            for(int j=0; j<GameData.COLUMNS; j++) {
                if(gameData.getGameArray()[0][j]=='W') {
                    int rowPosition = gameData.addInGameArray("User", j);
                    this.setPlayerTurn("User");

                    ArrayList<Integer> value = minMaxWithAlphaBeta(gameData, depth+1, true, alpha, beta);

                    this.setPlayerTurn("User");
                    gameData.removeFromGameArray(rowPosition, j);

                    if(worstEvaluation.get(0)>value.get(0)) {  //chooses the worst column and worst score for User
                        worstEvaluation.add(0,value.get(0));
                        worstEvaluation.add(1,j);
                    }
                    if(beta>worstEvaluation.get(0)) {
                        beta=worstEvaluation.get(0);
                    }
                    if(alpha>=beta) {
                        break;
                    }
                }
            }
            return worstEvaluation;
        }
    }
}
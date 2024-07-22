import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class AI extends PlayerMap{

    ArrayList<TreeMap<String, ArrayList<Integer>>> possibleBoardStates = new ArrayList<>();
    ArrayList<Boolean> possibleExtraTurn = new ArrayList<>();

    public AI(String name, String oppName, TreeMap<String, ArrayList<Integer>> pieceLocations){
        super(name, oppName, pieceLocations);
    }   

    /**
     * This method allows the ai to move
     * It takes in a roll and creates all the possible game scenarios with that roll
     * It then calculates the best future state and updates the board with this as its move
     * @param roll the roll value
     * @param selectedMove an unecessary param for the ai, but is necessary to override normal players move method
     * @param pieceLocations the current board state
     * @return boolean whether or not the player gets another turn
     */
    @Override
    public boolean move(int roll, int selectedMove, TreeMap<String, ArrayList<Integer>> pieceLocations){
        possibleBoardStates.clear();
        possibleExtraTurn.clear();
        setPieceLocations(pieceLocations);
        System.out.println();
        System.out.println("Playing: Black");
        System.out.println("You rolled a: " + roll);
        createPossibleBoardStates(roll);
        //go through possible states
        //set game model to the best possible set 
        int bestState = calculateStateValue();
        setPieceLocations(possibleBoardStates.get(bestState));
        return possibleExtraTurn.get(bestState);
    }

    /**
     * Loops through each piece in the old board state and checks if moving that piece with the given roll
     * results in a valid board state. If it does, it adds it to the possibleBoardStates array
     * @param roll given roll value
     */
    public void createPossibleBoardStates(int roll){
        possibleBoardStates.add(getPieceLocation());
        possibleExtraTurn.add(false);
        ArrayList<Integer> oldState = new ArrayList<Integer>(getPieceLocation().get(getName()));
        ArrayList<Integer> oppState = new ArrayList<Integer>(getPieceLocation().get(getOppName()));
        TreeSet<Integer> tilesWithPieces = new TreeSet<Integer>(oldState);
        for (int piece : tilesWithPieces){
            int newLocation = piece + roll;
            if (checkChoice(piece, roll)){
                ArrayList<Integer> newState = new ArrayList<Integer>(oldState);
                ArrayList<Integer> newOppState = new ArrayList<Integer>(oppState);
                newState.remove(oldState.indexOf(piece));
                newState.add(newLocation);
                //capture
                if (newLocation >= 5 && newLocation <= 12 && oppState.contains(newLocation)){
                    newOppState.remove(newOppState.indexOf(newLocation));
                    newOppState.add(0);
                }
                if (newLocation == 4 || newLocation == 8 || newLocation == 14){
                    possibleExtraTurn.add(true);
                }
                else{
                    possibleExtraTurn.add(false);
                }
                TreeMap<String, ArrayList<Integer>> newBoardState = new TreeMap<>();
                newBoardState.put(getName(), newState);
                newBoardState.put(getOppName(), newOppState);
                System.out.println(newState);
                possibleBoardStates.add(newBoardState);
            }
        }
    }

    /**
     * Calculates the value for every possible board state and finds the best one
     * @return int that corresponds to the index in possibleBoardStates for the best possible move
     */
    public int calculateStateValue(){
        TreeMap<String, ArrayList<Integer>> oldState = new TreeMap<>(getPieceLocation());
        double bareOffValue = 10.0;
        double rosetteValue = 20.0;
        double capturevalue = 10.0;

        int bestStateIndex = 0;
        double bestStateValue = -5000000.0;
        for (int n = 1; n < possibleBoardStates.size(); n++){
            double stateValue = 0;
            //takes nth future state
            TreeMap<String, ArrayList<Integer>> testState = new TreeMap<>(possibleBoardStates.get(n));
            //checks your pieces for their value 
            ArrayList<Integer> pieces = new ArrayList<>(testState.get(getName()));
            ArrayList<Integer> oppPieces = new ArrayList<>(testState.get(getOppName()));

            //adds up piece value for each piece on the board
            for (int piece : pieces){
                double chanceOfCapture = 0;
                stateValue += (double)(piece * 15)/14;
                //checks chance of capture when in danger zone
                if (piece >= 5 && piece <= 12){
                    if (oppPieces.contains(piece - 1)){
                        chanceOfCapture += (1/4);
                    }
                    if (oppPieces.contains(piece - 2)){
                        chanceOfCapture += (3/8);
                    }
                    if (oppPieces.contains(piece - 3)){
                        chanceOfCapture += (1/4);
                    }
                    if (oppPieces.contains(piece - 4)){
                        chanceOfCapture += (1/16);
                    }
                    stateValue -= (chanceOfCapture + 1) * 10;
                }
                //checks if  off board
                if (piece == 15){
                    stateValue += bareOffValue;
                }
                
            }
            //checks if gets extra turn
            if (possibleExtraTurn.get(n)){
                stateValue += rosetteValue;
            }
            //checks if captured opponent piece
            ArrayList<Integer> oldOppPieces = oldState.get(getOppName());
            if (!oppPieces.equals(oldOppPieces)){
                stateValue += capturevalue;
            }
            //if this state is the best, store it as the best
            if (stateValue > bestStateValue){
                bestStateIndex = n;
                bestStateValue = stateValue;
            }
            System.out.println((n) + ": " + stateValue + " move again: " + possibleExtraTurn.get(n));
        }
        System.out.println(bestStateIndex);
        return bestStateIndex;
    }

    /**
     * @return the size of the possibleBoardState array
     */
    public int getPossibleStateSize(){
        return possibleBoardStates.size();
    }
}

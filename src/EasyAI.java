import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Random;

public class EasyAI extends AI{
    public EasyAI(String name, String oppName, TreeMap<String, ArrayList<Integer>> pieceLocations){
        super(name, oppName, pieceLocations);
    }

    /**
     * Returns a random integer in the range of the size of the possibleBoardStates array
     * This means this AI is just making a random valid move
     */
    @Override
    public int calculateStateValue(){
        Random rand = new Random();
        int size = getPossibleStateSize();
        if (size == 1){
            return 0;
        }
        else{
            return rand.nextInt(1, size);
        }
    }
}
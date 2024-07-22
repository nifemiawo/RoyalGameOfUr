import java.util.TreeMap;
import java.util.ArrayList;

public class PlayerMap{
    private Tile[] map = new Tile[16];
    private String name;
    private String oppName;
    private TreeMap<String, ArrayList<Integer>> pieceLocations;

    public PlayerMap(String name, String oppName, TreeMap<String, ArrayList<Integer>> pieceLocations) {
        this.name = name;
        this.oppName = oppName;
        this.pieceLocations = pieceLocations;
        for (int index = 0; index < map.length; index++) {
            if (index >= 5 && index <= 12) {
                map[index] = new Tile(true);
            } else {
                map[index] = new Tile(false);
            }
        }
        map[4] = new Rosette(false);
        map[14] = new Rosette(false);
        map[8] = new Rosette(true);
    }

    public TreeMap<String, ArrayList<Integer>> getPieceLocation() {
        return this.pieceLocations;
    }

    public boolean move(int roll, int selectedMove, TreeMap<String, ArrayList<Integer>> pieceLocations) {
        //System.out.println(pieceLocations.get(oppName));
        setPieceLocations(pieceLocations);
        boolean turnStillGoing = false;
        int index = selectedMove;

        ArrayList<Integer> currentPlayerLocations = this.pieceLocations.get(name);
        currentPlayerLocations.remove(currentPlayerLocations.indexOf(index));
        turnStillGoing = map[index + roll].landedOn();
        currentPlayerLocations.add(index + roll);
        this.pieceLocations.put(name, currentPlayerLocations);
        if (map[index + roll].getMiddleRow() && checkOpponent(index + roll)) {
            ArrayList<Integer> opponentPlayerLocations = this.pieceLocations.get(oppName);
            System.out.println("test " + opponentPlayerLocations.indexOf(index + roll));
            opponentPlayerLocations.remove(opponentPlayerLocations.indexOf(index + roll));
            opponentPlayerLocations.add(0);
            this.pieceLocations.put(oppName, opponentPlayerLocations);
        }
        System.out.println("Moved from " + index + " to " + Integer.toString(index + roll));

        return turnStillGoing;
    }
    
    public boolean checkChoice(int index, int roll) {
        if ((pieceLocations.get(this.name)).contains(index) && index + roll <= 15 && (!(pieceLocations.get(this.name)).contains(index + roll) || index + roll == 15)){
            return true;
        } else {
            return false;
        }
    }
    
    public boolean checkIfValidChoiceAvailable(int roll) {
        ArrayList<Integer> currentPlayerLocations = pieceLocations.get(name);
        for (int index : currentPlayerLocations) {
            if (index + roll <= 15 && !currentPlayerLocations.contains(index + roll)) {
                return true;
            } else if (index + roll == 15) {
                return true;
            }
        }
        return false;
    }

    public boolean gameOver() {
        ArrayList<Integer> currentPlayerLocations = pieceLocations.get(name);
        for (int index : currentPlayerLocations) {
            if (index != 15) {
                return false;
            }
        }
        return true;
    }

    public boolean checkOpponent(int index) {
        if ((pieceLocations.get(oppName)).contains(index)) {
            return true;
        } else {
            return false;
        }
    }

    public void capture(int index, int roll){
        if (map[index + roll].getMiddleRow() && checkOpponent(index + roll)){
            ArrayList<Integer> opponentPlayerLocations = this.pieceLocations.get(oppName);
            System.out.println("test " + opponentPlayerLocations.indexOf(index + roll));
            opponentPlayerLocations.remove(opponentPlayerLocations.indexOf(index + roll));
            opponentPlayerLocations.add(0);
            this.pieceLocations.put(oppName, opponentPlayerLocations);
        }
    }

    public String getName(){return this.name;}

    public String getOppName(){return this.oppName;}

    public TreeMap<String, ArrayList<Integer>> getPieceLocations(){return pieceLocations;}

    public void setPieceLocations(TreeMap<String, ArrayList<Integer>> pieceLocations){this.pieceLocations = pieceLocations;}
}
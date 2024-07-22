public class Tile {
    private boolean middleRow;

    public Tile(boolean middleRow){
        this.middleRow = middleRow;
    }

    public boolean landedOn(){
        return false;
    }

    public boolean getMiddleRow(){
        return middleRow;
    }

}



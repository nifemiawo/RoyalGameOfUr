public class Rosette extends Tile{
    public Rosette(boolean middleTile){
        super(middleTile);
    }

    /**
     * @return true when landed on because player gets another turn
     */
    public boolean landedOn(){
        return true;
    }
}
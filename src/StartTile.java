public class StartTile extends Tile {
    // private int startingPieces = 5;

    public StartTile() {
        super(false);
    }


    @Override
    public boolean landedOn() {
        return false;
    }
}
package monopoly;

public abstract class Cell {
    private boolean available = true;
    private String name;
    protected Player player;
    //retuen name of the cell
    public String getName() {
        return name;
    }
    //return the player who owns this cell
    public Player getOwner() {
        return player;
    }
    //return price for this cell	
    public int getPrice() {
        return 0;
    }
    //check availablity of this cell
    public boolean isAvailable() {
        return available;
    }

    public void playAction(MainController mainCtl) {};

    //set a cell to be owned or free
    public void setAvailable(boolean available) {
        this.available = available;
    }
	
    public void setName(String name) {
        this.name = name;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

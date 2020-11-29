package monopoly;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardController {
    //Number of the maximum players = 8
    public static final int MAX_PLAYER = 8;
    //Initiate the game board
    private GameBoard gameBoard;
    /* Colors of the players on the gameBoard */
    private final List<Color> playerColors = new ArrayList<>(Arrays.asList(
            new Color(255, 249, 102), /* Player 1 */
            new Color(66, 134, 244),  /* Player 2 */
            new Color(143, 99, 158),  /* Player 3 */
            new Color(209, 155, 20),  /* Player 4 */
            new Color(209, 96, 20),   /* Player 5 */
            new Color(120, 230, 30),  /* Player 6 */
            new Color(206, 57, 72),   /* Player 7 */
            new Color(72, 196, 188)   /* Player 8 */
    ));
    private int outOfGamePlayers = 0;   //Number of out players
    private int playerTurnIndex = 0;    //Turn of Player number #
    private final List<Player> players = new ArrayList<>(); 

    public BoardController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
    //this method for return current player
    public Player getCurrentPlayer() {
        return getPlayer(playerTurnIndex);
    }
    //postion of player in game board
    public int getCurrentPositionIndex(Player player) {
        Cell currentPosition = player.getPosition();
        return gameBoard.queryCellIndex(currentPosition.getName());
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
    //this method for return player new postion
    public int getNewPositionIndex(int positionIndex, int diceValue) {
        return (positionIndex + diceValue) % gameBoard.getCellSize();    
    }
    //return numbers of players
    public int getNumberOfPlayers() {
        return players.size();
    }
    //return number of outgame player 
    public int getOutOfGamePlayersNumber() {
        return outOfGamePlayers;
    }
    
    public Player getPlayer(int index) {
        return players.get(index);
    }
    //index for numbers of player to show turn for each player
    public int getPlayerIndex(Player player) {
        return players.indexOf(player);
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    //turn of player 
    public int getTurn() {
        return playerTurnIndex;
    }
    //move player from postion to another from the last index to the new index postion
    public void movePlayer(Player player, int diceValue) {
        int positionIndex = getCurrentPositionIndex(player);
        int newIndex = getNewPositionIndex(positionIndex, diceValue);
        if (newIndex <= positionIndex || diceValue > gameBoard.getCellSize())
            player.setMoney(player.getMoney() + 200);
        player.setPosition(gameBoard.getCell(newIndex));
    }
    //remove a player
    public void removePlayer() {
        outOfGamePlayers++;
    }
    //reset to start from begining
    public void reset() {    
        for (int i = 0; i < getNumberOfPlayers(); i++) {
            Player player = players.get(i);
            player.setPosition(gameBoard.getCell(0));
        }
        playerTurnIndex = 0;
    }
    
    public void setGameBoard(GameBoard board) {
        this.gameBoard = board;
    }
    
    //create players for the given number
    public void setNumberOfPlayers(int number) {
        players.clear();
        for (int i = 0; i < number; i++) {
            Player player = new Player(gameBoard.getCell(0));
            player.setPlayerColor(playerColors.get(i));
            players.add(player);
        }
    }
    //switch turn from player to another
    public void switchTurn() {
        playerTurnIndex = (playerTurnIndex + 1) % getNumberOfPlayers();
    }
}

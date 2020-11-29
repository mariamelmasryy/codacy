package monopoly;

import java.util.List;
import monopoly.cells.CardCell;
import monopoly.cells.JailCell;
import monopoly.enums.CardType;
import monopoly.enums.ColorGroup;
import monopoly.gameboards.GameBoardDefault;
import monopoly.gui.MonopolyGUI;
import monopoly.gui.PlayerPanel;

public class MainController {
    private final BoardController boardController;

    private final Dice dice;
    private GameBoard gameBoard;
    private MonopolyGUI gui;
    private final PropertyController propertyController;
    private int utilityDiceRoll;
    
    public MainController() {
        gameBoard = new GameBoardDefault();
        boardController = new BoardController(gameBoard);
        propertyController = new PropertyController(boardController);
        dice = new Dice(2);     //the game with 2 dices
    }

    //show the dialog for buying
    public void buttonBuyHouseClicked() {
        gui.showBuyHouseDialog(getCurrentPlayer());
    }
    
    
    public Card buttonDrawCardClicked() {
        gui.setDrawCardEnabled(false); //lock the "Draw" button
        CardCell cell = (CardCell)getCurrentPlayer().getPosition();
        Card card;
        if (cell.getType() == CardType.CC) {
            card = getGameBoard().drawCCCard();
            card.applyAction(this);
        } else {
            card = getGameBoard().drawChanceCard();
            card.applyAction(this);
        }
        gui.setEndTurnEnabled(true); //unlock the "End" button
        return card;
    }

    public void buttonEndTurnClicked() {
        setAllButtonEnabled(false); //all buttons loced after end turn
        getCurrentPlayer().getPosition().playAction(this);
        
        if (getCurrentPlayer().isBankrupt()) {
            getCurrentPlayer().setOutOfGame();
            boardController.removePlayer();
        }
        switchTurn();
        gui.update(); //update the changes
    }

    public void buttonGetOutOfJailClicked() {
        getOutOfJail();
        if (getCurrentPlayer().isBankrupt()) {
            setAllButtonEnabled(false);
            getCurrentPlayer().setOutOfGame();
            int positionIndex = boardController.getCurrentPositionIndex(getCurrentPlayer());
            gui.removePlayer(getPlayerIndex(getCurrentPlayer()), positionIndex);
            boardController.removePlayer();
            switchTurn();
            gui.update();
        } else {
            gui.setRollDiceEnabled(true);
            gui.setBuyHouseEnabled(propertyController.canBuyHouse());
            gui.setGetOutOfJailEnabled(getCurrentPlayer().isInJail());
            gui.setTradeEnabled(getTurn(), true);
        }
    }

    public void buttonPurchasePropertyClicked() {
        purchase();// to buy something
        gui.setPurchasePropertyEnabled(false);
        gui.update();
    }
    
    //the value of the 2 dices
    public void buttonRollDiceClicked(PlayerPanel panel) {
        dice.roll();
        if ((dice.getTotal()) > 0) {
            Player player = getCurrentPlayer();
            gui.setRollDiceEnabled(false);
            StringBuilder msg = new StringBuilder();
            msg.append("You rolled ")
                    .append(dice.getSingleDice(0))
                    .append(" and ")
                    .append(dice.getSingleDice(1));
            gui.showMessage(msg.toString(), panel);
            movePlayer(player, dice.getTotal());
            gui.setBuyHouseEnabled(false);
        }
    }

    //show dialog for trading
    public void buttonTradeClicked() {
        TradeDialog dialog = gui.openTradeDialog();
        TradeDeal deal = dialog.getTradeDeal(this);
        if (deal != null) {
            RespondDialog respondDialog = gui.openRespondDialog(deal);
            if (respondDialog.getResponse()) {
                completeTrade(deal);
                gui.update();
            }
        }
    }
    
    //check the availabilty of buying this house
    public boolean canBuyHouse() {
        return propertyController.canBuyHouse();
    }

    //sell or buy a cell
    public void completeTrade(TradeDeal deal) {
        propertyController.sellProperty(deal);
        propertyController.buyProperty(deal);
    }

    //drawing a cc card
    public Card drawCCCard() {
        return gameBoard.drawCCCard();
    }

    //drawing a chance card
    public Card drawChanceCard() {
        return gameBoard.drawChanceCard();
    }
    
    private void finishPlayerMove(Player player) {
        Cell cell = player.getPosition();
        int playerIndex = getPlayerIndex(player);
        //check is this cell available for buying
        if (cell instanceof CardCell) {
            gui.setDrawCardEnabled(true);
        } else {
            if (cell.isAvailable()) {
                int price = cell.getPrice();
                if (price <= player.getMoney() && price > 0) //check that the player has money
                    gui.enablePurchaseButton(playerIndex); //unlock the "Buy" button
            }
            gui.enableEndTurnButton(playerIndex);
        }
        gui.setTradeEnabled(boardController.getTurn(), false);
    }

    //return the player for this turn
    public Player getCurrentPlayer() {
        return boardController.getCurrentPlayer();
    }
    
    //return the value of this dice
    public Dice getDice() {
        return dice;
    }

    public MonopolyGUI getGUI() {
        return gui;
    }
    
    public GameBoard getGameBoard() {
        return gameBoard;
    }
    
    //return list of players
    public List<ColorGroup> getMonopolies(Player player) {
        return propertyController.getMonopolies(player);
    }

    public int getNumberOfPlayers() {
        return boardController.getNumberOfPlayers();
    }
    
    private void getOutOfJail() {
        Player currentPlayer = boardController.getCurrentPlayer();
        currentPlayer.subtractMoney(JailCell.BAIL);
        if (currentPlayer.isBankrupt()) {
            currentPlayer.setMoney(0);
            giveAllProperties(currentPlayer, null);
        }
        currentPlayer.setInJail(false);
        gui.update();
    }

    public Player getPlayer(int index) {
        return boardController.getPlayer(index);
    }

    public int getPlayerIndex(Player player) {
        return boardController.getPlayerIndex(player);
    }

    public List<Player> getSellerList() {
        return propertyController.getSellerList();
    }
    
    public int getTurn() {
        return boardController.getTurn();
    }

    public int getUtilityDiceRoll() {
        return this.utilityDiceRoll;
    }
    
    public void giveAllProperties(Player fromPlayer, Player toPlayer) {
        propertyController.giveAllProperties(fromPlayer, toPlayer);
    }

    //move the player from place to another one
    public void movePlayer(Player player, int diceValue) {
        int positionIndex = boardController.getCurrentPositionIndex(player);
        int newIndex = boardController.getNewPositionIndex(positionIndex, diceValue);
        
        boardController.movePlayer(player, diceValue);
        gui.movePlayer(getPlayerIndex(player), positionIndex, newIndex);
        finishPlayerMove(player);

        gui.update();
    }
    
    public void payRentTo(Player owner, int rent) {
        propertyController.payRentTo(owner, rent);
    }
    
    public void purchase() {
        propertyController.purchase();
    }
    
    public void purchaseHouse(ColorGroup selectedMonopoly, int houses) {
        if (propertyController.purchaseHouse(selectedMonopoly, houses) <= 5)
            gui.update();
    }

    public void reset() {
        boardController.reset();
        if (gameBoard != null)
            gameBoard.removeCards();
    }
	
    public void sendToJail(Player player) {
        String currentPlayerName = getCurrentPlayer().getPosition().getName();
        int oldPosition = gameBoard.queryCellIndex(currentPlayerName);
        player.setPosition(gameBoard.queryCell("Jail"));
        player.setInJail(true);
        int jailIndex = gameBoard.queryCellIndex("Jail");
        gui.movePlayer(getPlayerIndex(player), oldPosition, jailIndex);
    }
    
    private void setAllButtonEnabled(boolean enabled) {
        gui.setRollDiceEnabled(enabled);
        gui.setPurchasePropertyEnabled(enabled);
        gui.setEndTurnEnabled(enabled);
        gui.setTradeEnabled(boardController.getTurn(), enabled);
        gui.setBuyHouseEnabled(enabled);
        gui.setDrawCardEnabled(enabled);
        gui.setGetOutOfJailEnabled(enabled);
    }
    
    public void setGUI(MonopolyGUI gui) {
        this.gui = gui;
    }

    public void setGameBoard(GameBoard board) {
        this.gameBoard = board;
        boardController.setGameBoard(board);
    }

    public void setNumberOfPlayers(int number) {
        boardController.setNumberOfPlayers(number);
    }

    public void startGame() {
        gui.startGame();
        gui.enablePlayerTurn(0);
        gui.setTradeEnabled(0, true);
    }

    public void switchTurn() {
        boardController.switchTurn();
        
        if (getCurrentPlayer().isOutOfGame()) {
            switchTurn();
            return;
        }
        if (boardController.getOutOfGamePlayersNumber() + 1 >= boardController.getNumberOfPlayers()) {
            setAllButtonEnabled(false);
            return;
        }
        if (!getCurrentPlayer().isInJail()) {
            gui.enablePlayerTurn(boardController.getTurn());
            gui.setBuyHouseEnabled(propertyController.canBuyHouse());
            gui.setTradeEnabled(boardController.getTurn(), true);
        } else {
            gui.setGetOutOfJailEnabled(true);
        }
    }

    public void utilityRollDice() {
        this.utilityDiceRoll = gui.showUtilityDiceRoll();
    }
    
}

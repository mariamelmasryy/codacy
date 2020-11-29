package monopoly;

import java.util.Arrays;

public class Dice {
    private static final int DICE_SIDES = 6;
    private final int[] dice;
    
    public Dice(int diceAmount) {
        dice = new int[diceAmount];
        roll();
    }
    //to clarify the number moves of a player
    public int[] getRoll() {
        roll();
        return dice;
    }
    //return postion for each player
    public int getSingleDice(int diceNumber) {
        return dice[diceNumber];
    }
    //sum of two number return when player press on roll dice button
    public int getTotal() {
        return Arrays.stream(dice).sum();
    }
    //getting the random number of the rolling
    public final void roll() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = (int)(Math.random() * DICE_SIDES) + 1;
        }
    }
    //set the value of the dice
    public void setDice(int diceNumber, int value) {
        dice[diceNumber] = value;
    }
}
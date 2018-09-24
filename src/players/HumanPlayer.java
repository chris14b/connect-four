package players;

import elements.Colour;
import elements.Grid;

import java.util.Scanner;

/**
 * @author Chris Johnston
 */
public class HumanPlayer extends Player {
    public HumanPlayer(Colour colour) {
        super(colour);
    }

    public int chooseColumn() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Column: ");
        return scanner.nextInt() - 1;
    }
}

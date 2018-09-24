package players;

import elements.Colour;
import elements.Grid;

/**
 * @author Chris Johnston
 */
public abstract class Player {
    Colour myColour;

    Player(Colour colour) {
        if (colour == Colour.NONE) {
            throw new IllegalArgumentException("Player's colour cannot be NONE.");
        }

        this.myColour = colour;
    }

    public Colour getColour() {
        return myColour;
    }

    public abstract int chooseColumn();
}

package elements;

import players.ComputerPlayer;
import players.HumanPlayer;
import players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Johnston
 */
public class Game {
    private Grid grid;
    private List<Player> players;
    private Player currPlayer;

    private Game(Grid grid, List<Player> players) {
        final int numPlayers = players.size();
        final int maxPlayers = Colour.values().length;

        if (!(2 <= numPlayers && numPlayers <= maxPlayers)) {
            throw new IllegalArgumentException("Number of players must be between 2 and " + maxPlayers + ".");
        }

        this.grid = grid;
        this.players = players;
        currPlayer = players.get(0);
    }

    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        List<Colour> colours = new ArrayList<>(2);
        colours.add(Colour.BLUE);
        colours.add(Colour.YELLOW);
        Grid grid = new Grid(colours);
//        players.add(new HumanPlayer(colours.get(0)));
        players.add(new ComputerPlayer(colours.get(0), colours.get(1), grid, 7));
        players.add(new ComputerPlayer(colours.get(1), colours.get(0), grid, 7));
        Game game = new Game(grid, players);
        game.run();
    }

    private void run() {
        Colour winner = null;

        while (winner == null) {
            System.out.println();
            grid.show();
            System.out.println();
            System.out.println();
            System.out.println();

            System.out.println(currPlayer.getColour().getName() + " player's turn");
            grid.placeDisc(currPlayer.getColour(), currPlayer.chooseColumn());
            winner = grid.getWinner();

            if (winner == null) {
                updateCurrPlayer();
            }
        }

        if (winner != Colour.NONE) {
            System.out.println("Game over! " + currPlayer.getColour().getName() + " player won!");
        } else {
            System.out.println("Game tied!");
        }

        System.out.println();
        grid.show();
    }

    private void updateCurrPlayer() {
        currPlayer = players.get(getCurrPlayerNumber() % players.size());
    }

    private int getCurrPlayerNumber() {
        return players.indexOf(currPlayer) + 1;
    }
}

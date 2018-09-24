package players;

import elements.Colour;
import elements.Grid;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Chris Johnston
 */
public class ComputerPlayer extends Player {
    private final Grid grid;
    private final Colour opponent;
    private final int maxDepth;

    public ComputerPlayer(Colour colour, Colour opponent, Grid grid, int maxDepth) {
        super(colour);
        this.opponent = opponent;
        this.grid = grid;
        this.maxDepth = maxDepth;
    }

    public int chooseColumn() {
        int move = getWinningMove(grid, super.myColour);
        int returnCol;

        if (move != -1) {
            returnCol = move;
        } else {

            List<Pair<Integer, Integer>> scores = new ArrayList<>(grid.getNumCols());

            for (int col = 0; col < grid.getNumCols(); col++) {
                updateLoadingBar((float) col / grid.getNumCols());

                if (grid.columnFull(col)) {
                    continue;
                }

                Grid nextGrid = grid.getGridIfDiscPlaced(super.myColour, col);
                scores.add(new Pair<>(col, getScore(nextGrid, 1)));
            }

            updateLoadingBar(1);

            for (Pair<Integer, Integer> score: scores) {
                System.out.println("Column " + score.getKey() + ": score = " + score.getValue());
            }

            returnCol = getKeyOfMax(scores);
        }

        int outputCol = returnCol + 1;
        System.out.println("Column: " + outputCol);
        return returnCol;
    }

    private int getScore(Grid grid, int currDepth) {
        if (currDepth > maxDepth) {
            return 0;
        }

        Colour winner = grid.getWinner();

        if (winner == grid.getLastColour()) {
//            System.out.println("Win detected for " + grid.getWinner() + " at depth " + currDepth);
//            grid.show();
            int score = maxDepth - currDepth + 1;

            if (winner == super.myColour) {
                return score;
            } else {
                return - score;
            }
        }

        int winningMove = getWinningMove(grid, grid.getLastColour());

        if (winningMove == -1) {
            List<Pair<Integer, Integer>> scores = new ArrayList<>(grid.getNumCols());

            for (int col = 0; col < grid.getNumCols(); col++) {
                if (grid.columnFull(col)) {
                    continue;
                }

                Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), col);
                int score = getScore(nextGrid, currDepth + 1);
                scores.add(new Pair<>(col, score));
            }

            return getMostExtreme(scores);
        } else {
            Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), winningMove);
            return getScore(nextGrid, currDepth + 1);
        }
    }

    private static int getWinningMove(Grid grid, Colour colour) {
        for (int col = 0; col < grid.getNumCols(); col++) {
            if (grid.columnFull(col)) {
                continue;
            }

            if (grid.getGridIfDiscPlaced(colour, col).getWinner() == colour) {
                return col;
            }
        }

        return -1;
    }

    private static int getMostExtreme(List<Pair<Integer, Integer>> list) {
        if (list.size() == 0) {
            return 0;
        }

        Pair<Integer, Integer> max = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            Pair<Integer, Integer> curr = list.get(i);

            if (Math.abs(curr.getValue()) > Math.abs(max.getValue())) {
                max = curr;
            }
        }

        return max.getValue();
    }

    private static int getKeyOfMax(List<Pair<Integer, Integer>> list) {
        List<Pair<Integer, Integer>> maxes = new ArrayList<>(list.size());
        int maxValue = list.get(0).getValue();

        for (Pair<Integer, Integer> curr: list) {
            if (curr.getValue() >= maxValue) {
                if (curr.getValue() > maxValue) {
                    maxes.clear();
                    maxValue = curr.getValue();
                }

                maxes.add(curr);
            }
        }

        return maxes.get(ThreadLocalRandom.current().nextInt(0, maxes.size())).getKey();
    }

    private void updateLoadingBar(float progress) {
        int width = grid.getNumCols() * 5 - 1;

        if (progress < 1) {
            System.out.print("\r|");

            for (float i = 0; i < width; i++) {
                if (i / width < progress) {
                    System.out.print("=");
                } else {
                    System.out.print(" ");
                }
            }

            System.out.print("|");
        } else {
            System.out.print("\r            \r");
        }
    }
}

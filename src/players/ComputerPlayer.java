package players;

import elements.Colour;
import elements.Grid;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Chris Johnston
 */
public class ComputerPlayer extends Player {
    private final Grid grid;
    private final int maxDepth;

    public ComputerPlayer(Colour colour, Grid grid, int maxDepth) {
        super(colour);
        this.grid = grid;
        this.maxDepth = maxDepth;
    }

    public int chooseColumn() {
        int numCols = grid.getNumCols();
        int[] scores = new int[numCols];

        for (int col = 0; col < numCols; col++) {
            updateLoadingBar((float) col / numCols);
            scores[col] = -(maxDepth + 2);

            if (grid.columnFull(col)) {
                continue;
            }

            Grid nextGrid = grid.getGridIfDiscPlaced(super.myColour, col);
            scores[col] = getScore(nextGrid, 1);
        }

        updateLoadingBar(1);

//        for (int col = 0; col < numCols; col++) {
//            System.out.println("Column " + col + ": score = " + scores[col]);
//        }

        int returnCol = getIndexOfMax(scores);
        int outputReturnCol = returnCol + 1;
        System.out.println("Column: " + outputReturnCol);
        return returnCol;
    }

    private int getScore(Grid grid, int currDepth) {
        if (currDepth > maxDepth) {
            return 0;
        }

        int returnScore = maxDepth - currDepth + 1;
        Colour winner = grid.getWinner();

        if (winner != null && winner == grid.getLastColour()) {
//            System.out.println("Detected win for " + winner.toString() + " at depth " + currDepth);

            if (winner == super.myColour) {
                return returnScore;
            } else {
                return -returnScore;
            }
        }

        int numCols = grid.getNumCols();
        List<Pair<Integer, Integer>> scores = new ArrayList<>(numCols);

        for (int col = 0; col < numCols; col++) {
            if (grid.columnFull(col)) {
                continue;
            }

            Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), col);
            int score = getScore(nextGrid, currDepth + 1);

//            if (score != 0 && Math.abs(score) == returnScore - 2) {
//                System.out.println("Win will be blocked! " + score + " = " + returnScore + " - 2");
//            } else {
//                System.out.println("Win will NOT be blocked");
//            }

            if (currDepth == 1 || Math.abs(score) != returnScore - 2) {
                scores.add(new Pair<>(col, score));
            }
        }

        if (scores.size() > 0) {
            return getMostExtreme(scores);
        } else {
            for (int col = 0; col < numCols; col++) {
                if (grid.columnFull(col)) {
                    continue;
                }

                Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), col);
                return getScore(nextGrid, currDepth + 1);
            }
        }

        return 1000; // TODO: fix
    }

    public int chooseColumnOld() {
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
                scores.add(new Pair<>(col, getScoreOld(nextGrid, 1)));
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

    private int getScoreOld(Grid grid, int currDepth) {
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
                if (currDepth == 2) {
                    System.out.println("Col = " + col);
                }

                if (grid.columnFull(col)) {
                    continue;
                }

                Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), col);
                int score = getScoreOld(nextGrid, currDepth + 1);

                if (score != 0 && currDepth == 2) {
                    System.out.println(score + " = " + maxDepth + " - " + currDepth);
                }

                if (currDepth != 1 && Math.abs(score) == maxDepth - currDepth) {
                    if (currDepth == 2) {
                        System.out.println("Continuing...");
                    }

                    continue;
                }

                if (currDepth == 2) {
                    System.out.println("Adding score");
                }

                scores.add(new Pair<>(col, score));
            }

            if (scores.size() == 0) {
                if (currDepth == 2) {
                    System.out.println("No viable moves at depth " + currDepth);
                    grid.show();
                }

                int score = maxDepth - currDepth + 1;

                if (grid.getNextNextColour() == super.myColour) {
                    return score;
                } else {
                    return - score;
                }
            } else {
//                System.out.println("Viable moves at depth " + currDepth);
            }

            return getMostExtreme(scores);
        } else {
            Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), winningMove);
            return getScoreOld(nextGrid, currDepth + 1);
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

    private static int getIndexOfMax(int[] array) {
        int maxValue = array[0];
        List<Integer> indexesOfMax = new ArrayList<>(array.length);

        for (int i = 0; i < array.length; i++) {
            if (array[i] >= maxValue) {
                if (array[i] > maxValue) {
                    maxValue = array[i];
                    indexesOfMax.clear();
                }

                indexesOfMax.add(i);
            }
        }

        return indexesOfMax.get(ThreadLocalRandom.current().nextInt(0, indexesOfMax.size()));
    }
}

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
        updateLoadingBar(0);
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
                scores.add(new Pair<>(col, - getScore(nextGrid, 1)));
                System.out.println("Column " + col + ": score = " + scores.get(scores.size() - 1).getValue());
            }

            returnCol = getKeyOfMax(scores);
        }

        updateLoadingBar(1);
        int outputCol = returnCol + 1;
        System.out.println("Column: " + outputCol);
        return returnCol;
    }

    private int getScore(Grid grid, int currDepth) {
        if (currDepth > maxDepth) {
            return 0;
        }

        if (grid.getWinner() == grid.getLastColour()) {
//            System.out.println("Win detected for " + grid.getWinner() + " at depth " + currDepth);
            return maxDepth - currDepth + 1;
        }

        List<Pair<Integer, Integer>> scores = new ArrayList<>(grid.getNumCols());

        for (int col = 0; col < grid.getNumCols(); col++) {
            if (grid.columnFull(col)) {
                continue;
            }

            Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), col);
            scores.add(new Pair<>(col, - getScore(nextGrid, currDepth + 1)));
        }

        if (grid.getLastColour() == super.myColour) {
            return getMostExtreme(scores);
        } else {
            return getMax(scores);
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
        int max = 0;

        for (Pair<Integer, Integer> curr: list) {
            max = Math.max(Math.abs(max), Math.abs(curr.getValue()));
        }

        return max;
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

    private static int getMax(List<Pair<Integer, Integer>> list) {
        int maxValue = list.get(0).getValue();

        for (int i = 1; i < list.size(); i++) {
            maxValue = Math.max(maxValue, list.get(i).getValue());
        }

        return maxValue;
    }

    public int chooseColumnOld2() {
        List<Integer> moves = getWinningMoves(grid, super.myColour);
        int returnCol;

        if (moves.size() > 0) {
            System.out.println("Making winning move!");
            returnCol =  moves.get(0);
        } else {

            moves = getWinningMoves(grid, opponent);

            if (moves.size() > 0) {
                System.out.println("Blocking a loss");
                returnCol = moves.get(0);
            } else {

                float[] scores = new float[grid.getNumCols()];
                int numCols = grid.getNumCols();

                for (int col = 0; col < numCols; col++) {
                    updateLoadingBar((float) col / numCols);

                    if (grid.columnFull(col)) {
                        continue;
                    }

                    Grid nextGrid = grid.getGridIfDiscPlaced(super.myColour, col);
                    scores[col] = getScoreOld2(nextGrid, 2);
                    System.out.println("scores[" + col + "] = " + scores[col]);
                }

                updateLoadingBar(1);

                List<Integer> indexesOfMax = getIndexesOfMax(scores);
                returnCol = indexesOfMax.get(ThreadLocalRandom.current().nextInt(0, indexesOfMax.size()));
            }
        }

        int outputColNo = returnCol + 1;
        System.out.println("Column: " + outputColNo);
        return returnCol;
    }

    private List<Integer> getWinningMoves(Grid grid, Colour colour) {
        List<Integer> winningMoves = new ArrayList<>(2);

        for (int col = 0; col < grid.getNumCols(); col++) {
            if (grid.columnFull(col)) {
                continue;
            }

            Colour winner = grid.getGridIfDiscPlaced(colour, col).getWinner();

            if (winner != null && winner == colour) {
//                grid.show();
                winningMoves.add(col);
            }
        }

        return winningMoves;
    }

    private float getScoreOld2(Grid grid, int currDepth) {
        List<Integer> moves = getWinningMoves(grid, grid.getNextColour());

        if (moves.size() >= 2) {
            System.out.println("Detected a double win at depth " + currDepth);
            grid.show();
            return maxDepth - currDepth + 1;
        }

        moves = getWinningMoves(grid, grid.getNextNextColour());

        if (moves.size() >= 2) {
            System.out.println("Detected a double loss at depth " + currDepth);
            grid.show();
            return - (maxDepth - currDepth + 1);
        }

        if (currDepth < maxDepth) {
            int numCols = grid.getNumCols();

            if (moves.size() == 0) {
                float[] scores = new float[grid.getNumCols()];

                for (int col = 0; col < numCols; col++) {
                    if (grid.columnFull(col)) {
                        continue;
                    }

                    Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), col);
                    scores[col] = getScoreOld2(nextGrid, currDepth + 1);
                }

//                if (grid.getNextColour() != super.myColour) {
                    return convertToSingleScore(scores);
//                } else {
//                    return getArrayMax(scores);
//                }
            } else {
                Grid nextGrid = grid.getGridIfDiscPlaced(grid.getNextColour(), moves.get(0));
                return getScoreOld2(nextGrid, currDepth + 1);
            }
        } else {
            return 0;
        }
    }

    public int chooseColumnOld() {
        float[] scores = new float[grid.getNumCols()];
        int numCols = grid.getNumCols();

        for (int col = 0; col < numCols; col++) {
            updateLoadingBar((float) col / numCols);

            if (grid.columnFull(col)) {
                continue;
            }

            scores[col] = 0;

            Grid nextGrid = grid.getGridIfDiscPlaced(super.myColour, col);
            Colour winner = nextGrid.getWinner();

            if (winner == super.myColour) {
                scores[col] = 1;
            } else if (winner == null) {
                scores[col] = getScoreOld(nextGrid, 2) / 2;
            }
        }

        updateLoadingBar(1);

        List<Integer> indexesOfMax = getIndexesOfMax(scores);
        int col = indexesOfMax.get(ThreadLocalRandom.current().nextInt(0, indexesOfMax.size()));
        int outputColNo = col + 1;
        System.out.println("Column: " + outputColNo);
        return col;
    }

    private float getScoreOld(Grid grid, int depth) {
        int numCols = grid.getNumCols();
        boolean won = false;

        Colour currColour;

        if (depth % 2 == 1) {
            currColour = super.myColour;
        } else {
            currColour = opponent;
        }

        Set<Grid> nextGrids = new HashSet<>(numCols);

        for (int col = 0; col < numCols; col++) {
            if (grid.columnFull(col)) {
                continue;
            }

            Grid nextGrid = grid.getGridIfDiscPlaced(currColour, col);
            nextGrids.add(nextGrid);
            Colour winner = nextGrid.getWinner();

            if (winner != null) {
                if (winner != Colour.NONE) {
                    won = true;
                    break;
                } else {
                    return 0;
                }
            }
        }

        if (won) {
            if (currColour == super.myColour) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (depth < maxDepth) {
                List<Float> scores = new ArrayList<>(numCols);

                for (Grid nextGrid : nextGrids) {
                    scores.add(getScoreOld(nextGrid, depth + 1) / 2);
                }

                if (currColour != super.myColour) {
                    return convertToSingleScore(scores);
                } else {
                    return getListMax(scores);
                }
            } else {
                return 0;
            }
        }
    }

    private static float getListMax(List<Float> list) {
        float max = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }

        return max;
    }

    private static float getArrayMax(float[] array) {
        float max = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }

        return max;
    }

    private static float convertToSingleScore(List<Float> list) {
        float max = Math.abs(list.get(0));
        int indexOfMax = 0;

        for (int i = 1; i < list.size(); i++) {
            if (Math.abs(list.get(i)) > max) {
                max = Math.abs(list.get(i));
                indexOfMax = i;
            }
        }

        return list.get(indexOfMax);
    }

    private static float convertToSingleScore(float[] array) {
        float max = Math.abs(array[0]);
        int indexOfMax = 0;

        for (int i = 1; i < array.length; i++) {
            if (Math.abs(array[i]) > max) {
                max = Math.abs(array[i]);
                indexOfMax = i;
            }
        }

        return array[indexOfMax];
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

    private List<Integer> getIndexesOfMax(float[] array) {
        float maxValue = 0;
        List<Integer> indexesOfMax = new ArrayList<>();
        boolean first = true;

        for (int col = 0; col < array.length; col++) {
            if (!grid.columnFull(col)) {
                if (first) {
                    maxValue = array[col];
                    indexesOfMax.add(col);
                    first = false;
                } else if (array[col] >= maxValue) {
                    if (array[col] > maxValue) {
                        indexesOfMax.clear();
                    }

                    maxValue = array[col];
                    indexesOfMax.add(col);
                }
            }
        }

        return indexesOfMax;
    }
}

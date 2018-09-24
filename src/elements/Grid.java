package elements;

import java.util.List;

/**
 * @author Chris Johnston
 */
public class Grid {
    private final int numCols;
    private final int numRows;
    private Colour[][] spaces;
    private int lastCol;
    private int lastRow;
    private Colour winner;
    private final List<Colour> colours;
    private Colour nextColour;

    Grid(List<Colour> colours) {
        this(6, 7, colours);
    }

    Grid(int numRows, int numCols, List<Colour> colours) {
        if (!(1 <= numRows && numRows <= 9)) {
            throw new IllegalArgumentException("Number of rows must be between 1 and 9");
        }

        if (!(1 <= numCols && numCols <= 9)) {
            throw new IllegalArgumentException("Number of columns must be between 1 and 9");
        }

        this.numRows = numRows;
        this.numCols = numCols;
        spaces = new Colour[numCols][numRows];

        for (int col = 0; col < numCols; col++) {
            for (int row = 0; row < numRows; row++) {
                spaces[col][row] = Colour.NONE;
            }
        }

        lastRow = -1;
        lastCol = -1;
        winner = null;
        this.colours = colours;
        nextColour = colours.get(0);
    }

    private Grid(Grid grid) {
        this.spaces = new Colour[grid.numCols][grid.numRows];

        for (int col = 0; col < grid.numCols; col++) {
            spaces[col] = grid.spaces[col].clone();
        }

        this.numCols = grid.numCols;
        this.numRows = grid.numRows;
        this.lastCol = grid.lastCol;
        this.lastRow = grid.lastRow;
        this.winner = grid.winner;
        this.colours = grid.colours;
        this.nextColour = grid.nextColour;
    }

    private static void printLine(int length) {
        for (int i = 0; i < length - 1; i++) {
            System.out.print("-");
        }

        System.out.println("-");
    }

    void placeDisc(Colour colour, int col) {
        if (!(0 <= col && col <= numCols)) {
            throw new IllegalArgumentException("Column " + col + " is not a valid column.");
        }

        if (columnFull(col)) {
            throw new IllegalArgumentException("Column " + col + " is full.");
        }

        int row = 0;

        while (spaces[col][row] != Colour.NONE) {
            row++;
        }

        setSpace(col, row, colour);
        lastCol = col;
        lastRow = row;
        winner = updateWinner();

        nextColour = colours.get((colours.indexOf(colour) + 1) % colours.size());
    }

    public void show() {
        for (int col = 0; col < numCols; col++) {
            int outputColNo = col + 1;
            System.out.print("| " + outputColNo + "  ");
        }

        System.out.println("|");

        printLine(numCols * 5 + 1);

        for (int row = numRows - 1; row >= 0; row--) {
            for (int i = 0; i < 2; i++) {
                for (int col = 0; col < numCols; col++) {
                    System.out.print("| ");
                    boolean bold = (col == lastCol && row == lastRow);
                    spaces[col][row].printDisc(bold);
                    spaces[col][row].printDisc(bold);
                    System.out.print(" ");
                }

                System.out.println("|");
            }
            printLine(numCols * 5 + 1);
        }
    }

    public Colour getWinner() {
        return winner;
    }

    private Colour updateWinner() {
        if (winner != null) {
            return winner;
        }

        if (lastRow == -1 || lastCol == -1) {
            return null;
        }

        if (lastRow == numRows - 1) {
            boolean tie = true;

            for (int i = 0; i < numCols; i++) {
                if (spaces[i][lastRow] == Colour.NONE) {
                    tie = false;
                    break;
                }
            }

            if (tie) {
                return Colour.NONE;
            }
        }

        Colour colour = spaces[lastCol][lastRow];

        if (checkWin(1, 0)) {
            return colour;
        } else if (checkWin(0, 1)) {
            return colour;
        } else if (checkWin(1, 1)) {
            return colour;
        } else if (checkWin(1, -1)) {
            return colour;
        } else {
            return null;
        }
    }

    private boolean checkWin(int colModifier, int rowModifier) {
        Colour colour = spaces[lastCol][lastRow];

        if (hasDisc(lastCol - colModifier, lastRow - rowModifier, colour)) {
            if (hasDisc(lastCol - 2 * colModifier, lastRow - 2 * rowModifier, colour)) {
                if (hasDisc(lastCol - 3 * colModifier, lastRow - 3 * rowModifier, colour)) {
                    return true;
                } else {
                    return hasDisc(lastCol + colModifier, lastRow + rowModifier, colour);
                }
            } else {
                if (hasDisc(lastCol + colModifier, lastRow + rowModifier, colour)) {
                    return hasDisc(lastCol + 2 * colModifier, lastRow + 2 * rowModifier, colour);
                } else {
                    return false;
                }
            }
        } else {
            if (hasDisc(lastCol + colModifier, lastRow + rowModifier, colour)) {
                if (hasDisc(lastCol + 2 * colModifier, lastRow + 2 * rowModifier, colour)) {
                    return hasDisc(lastCol + 3 * colModifier, lastRow + 3 * rowModifier, colour);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private boolean hasDisc(int col, int row, Colour colour) {
        return 0 <= col && col < numCols && 0 <= row && row < numRows && spaces[col][row] == colour;
    }

    public boolean columnFull(int col) {
        if (!(0 <= col && col < numCols)) {
            throw new IllegalArgumentException("Column " + col + " is not a valid column.");
        }

        return (spaces[col][numRows - 1] != Colour.NONE);
    }

    public int getNumCols() {
        return numCols;
    }

//    int getNumFilledSpaces() {
//        int count = 0;
//
//        for (int col = 0; col < numCols; col++) {
//            for (int row = 0; row < numRows; row++) {
//                if (spaces[col][row] != Colour.NONE) {
//                    count++;
//                }
//            }
//        }
//
//        return count;
//    }

//    int getNumSpaces() {
//        return numCols * numRows;
//    }

//    Colour getSpace(int col, int row) {
//        if (!(0 <= col && col < numCols)) {
//            throw new IllegalArgumentException("Column " + col + " is not a valid column.");
//        }
//
//        if (!(0 <= row && row < numRows)) {
//            throw new IllegalArgumentException("Row " + row + " is not a valid row.");
//        }
//
//        return spaces[col][row];
//    }

    private void setSpace(int col, int row, Colour colour) {
        if (!(0 <= col && col < numCols)) {
            throw new IllegalArgumentException("Column " + col + " is not a valid column.");
        }

        if (!(0 <= row && row < numRows)) {
            throw new IllegalArgumentException("Row " + row + " is not a valid row.");
        }

        spaces[col][row] = colour;
    }

    public Grid getGridIfDiscPlaced(Colour colour, int col) {
        Grid newGrid = new Grid(this);
        newGrid.placeDisc(colour, col);
        return newGrid;
    }

    public int getNumColours() {
        return colours.size();
    }

    public Colour getNextColour() {
        return nextColour;
    }

    public Colour getNextNextColour() {
        return colours.get((colours.indexOf(nextColour) + 1) % colours.size());
    }

    public Colour getLastColour() {
        return colours.get((colours.indexOf(nextColour) + getNumColours() - 1) % colours.size());
    }
}

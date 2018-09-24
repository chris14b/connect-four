package elements;

/**
 * @author Chris Johnston
 */
public enum Colour {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    NONE("\u001B[0m");

    private final String startString;

    Colour(String startString) {
        this.startString = startString;
    }

    public void printDisc(boolean bold) {
        if (this != NONE) {
            String disc = "@";

            if (this == BLUE) {
                disc = "X";
            }

            if (!bold) {
                System.out.print(colourString(disc));
            } else {
                System.out.print(colourString(boldString(disc)));
            }

        } else {
            System.out.print(" ");
        }
    }

    public String getName() {
        if (this == NONE) {
            throw new IllegalArgumentException("Colour NONE has no name");
        }

        String name = this.toString();
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return colourString(name);
    }

    private String colourString(String string) {
        if (this != NONE) {
            return startString + string + "\u001B[0m";
        } else {
            return string;
        }
    }

    private String boldString(String string) {
        if (this != NONE) {
            return "\u001B[1m" + string + "\u001B[0m";
        } else {
            return string;
        }
    }
}

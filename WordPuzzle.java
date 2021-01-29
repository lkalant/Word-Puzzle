// Levon Kalantarian
// Word Puzzle Solver

import java.util.Random;

public class WordPuzzle {

    private int rows;
    private int columns;
    private char[][] puzzle;

    public WordPuzzle() {
        this(20, 20);
    }

    public WordPuzzle(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        puzzle = new char[rows][columns];
        fillPuzzle();
    }

    private void fillPuzzle() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                puzzle[i][j] = (char) (97 + rand.nextInt(25));
            }
        }
    }

    public void printPuzzle() {
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.println();
        }
//        System.out.println(rows + "x" + columns);
        System.out.println();
    }

    public char getChar(int row, int column) {
        return puzzle[row][column];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}

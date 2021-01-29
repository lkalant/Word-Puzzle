// Levon Kalantarian
// Word Puzzle Solver

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MyHashTable<AnyType> extends WordPuzzle {

    private static final int DEFAULT_TABLE_SIZE = 101;

    private MyHashTable.HashEntry<AnyType>[] array;    // The array of elements
    private int occupied;                   // The number of occupied cells     occupied count
    private int theSize;                    // Current size                     visible size
    private int maxChars;


    private static class HashEntry<AnyType> {
        public AnyType element;             // the element
        public boolean isActive;            // false if marked deleted
        public boolean isWord;              // if the entry is a word

        public HashEntry(AnyType e) {
            this(e, true, false);
        }

        public HashEntry(AnyType e, boolean i, boolean w) {
            element = e;
            isActive = i;       // for lazy deletion
            isWord = w;
        }
    }

    public MyHashTable() {
        this(DEFAULT_TABLE_SIZE);
    }

    public MyHashTable(int size) {
        allocateArray(size);
        doClear();
        maxChars = 0;           // by default 0, unless used with strings
    }

    private void allocateArray(int arraySize) {
        array = new MyHashTable.HashEntry[nextPrime(arraySize)];
    }

    public boolean insert(AnyType x) {
        // Insert x as active
        int currentPos = findPos(x);
        if (isActive(currentPos)) {     // if active, found a duplicate
            return false;
        }

        if (array[currentPos] == null) {
            ++occupied;                 // increase slots occupied
        }
        array[currentPos] = new HashEntry<>(x, true, false);
        theSize++;

        // Rehash; see Section 5.5
        if (occupied > array.length / 2) {          // testing if # occupied cells exceeds 1/2 array size
            rehash();
        }

        return true;
    }

    // remove from the hash table
    public boolean remove(AnyType x) {
        int currentPos = findPos(x);
        if (isActive(currentPos)) {
            array[currentPos].isActive = false;     // mark as deleted
            theSize--;                              // reduce visible size, but not occupied count
            return true;
        } else {
            return false;
        }
    }

    private void rehash() {
        HashEntry<AnyType>[] oldArray = array;      // saves the old array

        // Create a new double-sized, empty table
        allocateArray(2 * oldArray.length);
        occupied = 0;
        theSize = 0;

        // Copy table, deleted entries not copied;  a kind of flushing effect of lazily deleted entries
        for (HashEntry<AnyType> entry : oldArray) {
            if (entry != null && entry.isActive) {
                insert(entry.element);
            }
        }
    }

    // linear probing
    private int findPos(AnyType x) {
        int offset = 1;
        int currentPos = myhash(x);

        while (array[currentPos] != null && !array[currentPos].element.equals(x)) { // if occupied and not duplicate
            currentPos += offset;   // Compute ith probe
            // offset remains at 1 for linear probing
//             offset += 2;            // taking the old position, adding 2 to offset, and adding offset to that
            if (currentPos >= array.length) {
                currentPos -= array.length; // if go off the end, wrap around
            }
        }
        return currentPos;
    }

    public int size() {
        return theSize;
    }

    public int capacity() {
        return array.length;
    }

    public boolean contains(AnyType x) {
        int currentPos = findPos(x);
        return isActive(currentPos);
    }

    private boolean isActive(int currentPos) {
        return array[currentPos] != null && array[currentPos].isActive;
    }

    // check if entry is a word
    private boolean isWord(int currentPos) {
        return array[currentPos] != null && array[currentPos].isWord;
    }

    public void makeEmpty() {
        doClear();
    }

    private void doClear() {
        occupied = 0;
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }

    private int myhash(AnyType x) {
        int hashVal = x.hashCode();

        hashVal %= array.length;
        if (hashVal < 0) {
            hashVal += array.length;
        }
        return hashVal;
    }

    /**
     * Internal method to find a prime number at least as large as n.
     *
     * @param n the starting number (must be positive).
     * @return a prime number larger than or equal to n.
     */
    private static int nextPrime(int n) {
        if (n % 2 == 0)
            n++;

        for (; !isPrime(n); n += 2)
            ;

        return n;
    }

    /**
     * Internal method to test if a number is prime.
     * Not an efficient algorithm.
     *
     * @param n the number to test.
     * @return the result of the test.
     */
    private static boolean isPrime(int n) {
        if (n == 2 || n == 3)
            return true;

        if (n == 1 || n % 2 == 0)
            return false;

        for (int i = 3; i * i <= n; i += 2)
            if (n % i == 0)
                return false;

        return true;
    }

    // get integer input
    private static int getInt() {
        Scanner scanner = new Scanner(System.in);
        int input = -1;
        boolean prompt = true;

        while (prompt) {
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                prompt = false;
            } else {
                System.out.println("Invalid input");
                scanner.nextLine();
            }
        }
        return input;
    }

    // first algorithm
    public void alg1(WordPuzzle newPuzzle) {

        System.out.println();
        long startTime = System.currentTimeMillis();
        StringBuilder sb;
        for (int row = 0; row < newPuzzle.getRows(); row++) {
            for (int col = 0; col < newPuzzle.getColumns(); col++) {

                // left to right
                sb = new StringBuilder();
                for (int t = col; t < newPuzzle.getColumns(); t++) {
                    sb.append(newPuzzle.getChar(row, t));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                }

                // right to left
                sb = new StringBuilder();
                for (int t = col; t > 0; t--) {
                    sb.append(newPuzzle.getChar(row, t));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                }

                // top down
                sb = new StringBuilder();
                for (int t = row; t < newPuzzle.getRows(); t++) {
                    sb.append(newPuzzle.getChar(t, col));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                }

                // bottom up
                sb = new StringBuilder();
                for (int t = row; t > 0; t--) {
                    sb.append(newPuzzle.getChar(t, col));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                }

                // bottom right
                sb = new StringBuilder();
                int t = row;
                int s = col;
                while (t < newPuzzle.getRows() && s < newPuzzle.getColumns()) {
                    sb.append(newPuzzle.getChar(t, s));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                    s++;
                    t++;
                }

                // top left
                sb = new StringBuilder();
                t = row;
                s = col;
                while (t > 0 && s > 0) {
                    sb.append(newPuzzle.getChar(t, s));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                    s--;
                    t--;
                }

                // top right
                sb = new StringBuilder();
                t = row;
                s = col;
                while (t > 0 && s < newPuzzle.getColumns()) {
                    sb.append(newPuzzle.getChar(t, s));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                    s++;
                    t--;
                }

                // bottom left
                sb = new StringBuilder();
                t = row;
                s = col;
                while (t < newPuzzle.getRows() && s > 0) {
                    sb.append(newPuzzle.getChar(t, s));
                    if (isWord(findPos((AnyType) sb.toString()))) {
                        System.out.print(sb.toString() + " ");
                    }
                    s--;
                    t++;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("\nElapsed time: " + (endTime - startTime) + "\n");
    }

    // enhanced algorithm
    public void alg2(WordPuzzle newPuzzle) {

        System.out.println();
        long startTime = System.currentTimeMillis();
        StringBuilder sb;
        for (int row = 0; row < newPuzzle.getRows(); row++) {
            for (int col = 0; col < newPuzzle.getColumns(); col++) {

                // left to right
                sb = new StringBuilder();
                String lookup;
                int chars = 0;
                for (int t = col; t < newPuzzle.getColumns(); t++) {
                    if (++chars > maxChars) {                  // if reached max char count, break
                        break;
                    }
                    sb.append(newPuzzle.getChar(row, t));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {          // if reached a non-prefix character sequence, break from loop
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                }

                // right to left
                chars = 0;
                sb = new StringBuilder();       // clear StringBuilder
                for (int t = col; t > 0; t--) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(row, t));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                }

                // top down
                chars = 0;
                sb = new StringBuilder();
                for (int t = row; t < newPuzzle.getRows(); t++) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(t, col));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                }

                // bottom up
                chars = 0;
                sb = new StringBuilder();
                for (int t = row; t > 0; t--) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(t, col));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                }

                // bottom right
                chars = 0;
                sb = new StringBuilder();
                int t = row;
                int s = col;
                while (t < newPuzzle.getRows() && s < newPuzzle.getColumns()) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(t, s));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                    s++;
                    t++;
                }

                // top left
                chars = 0;
                sb = new StringBuilder();
                t = row;
                s = col;
                while (t > 0 && s > 0) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(t, s));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                    s--;
                    t--;
                }

                // top right
                chars = 0;
                sb = new StringBuilder();
                t = row;
                s = col;
                while (t > 0 && s < newPuzzle.getColumns()) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(t, s));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                    s++;
                    t--;
                }

                // bottom left
                chars = 0;
                sb = new StringBuilder();
                t = row;
                s = col;
                while (t < newPuzzle.getRows() && s > 0) {
                    if (++chars > maxChars) {
                        break;
                    }
                    sb.append(newPuzzle.getChar(t, s));
                    lookup = sb.toString();
                    if (!contains((AnyType) lookup)) {
                        break;
                    }
                    int currentPos = findPos((AnyType) lookup);
                    if (isWord(currentPos)) {
                        System.out.print(lookup + " ");
                    }
                    s--;
                    t++;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nElapsed time: " + (endTime - startTime) + "\n");
    }

    // create a new puzzle
    public static WordPuzzle newPuzzle() {

        System.out.print("\nPlease enter puzzle rows (1-50): ");
        int rows = getInt();
        while (rows < 1 || rows > 50) {
            System.out.print("Invalid number.  Please enter rows (1-50): ");
            rows = getInt();
        }

        System.out.print("Please enter puzzle columns (1-50): ");
        int columns = getInt();
        while (columns < 1 || columns > 50) {
            System.out.print("Invalid number.  Please enter columns (1-50): ");
            columns = getInt();
        }

        return new WordPuzzle(rows, columns);
    }

    public static void printMenu() {
        System.out.print(
                "1 - run regular algorithm\n" +
                        "2 - run enhanced algorithm\n" +
                        "3 - re-make new puzzle\n" +
                        "4 - print puzzle\n" +
                        "5 - exit\n" +
                        "Please enter 1-5:   ");
    }

    public static MyHashTable<String> makeTable() throws FileNotFoundException {
        File file = new File("dictionary.txt");
        Scanner fileScanner = new Scanner(file);
        MyHashTable<String> H = new MyHashTable<>(520000);

        StringBuilder sb;
        String word, prefix = null;
        H.maxChars = 0;
        while (fileScanner.hasNextLine()) {
            int charCount = 0;
            sb = new StringBuilder();               // new dictionary entry; start new string
            word = fileScanner.nextLine();
            for (int i = 0; i < word.length(); i++) {
                sb.append(word.charAt(i));          // append new character from entry
                prefix = sb.toString();
                H.insert(prefix);                   // insert into hash table
                charCount++;
                if (charCount > H.maxChars) {
                    H.maxChars = charCount;
                }
            }
            int currentPos = H.findPos(prefix);
            H.array[currentPos].isWord = true;      // after reaching end of entry, mark as word
        }
        return H;
    }

    public static void main(String[] args) throws FileNotFoundException {

        WordPuzzle puzzle = newPuzzle();
        MyHashTable<String> H = makeTable();

        int choice;
        boolean running = true;
        puzzle.printPuzzle();

        while (running) {
            printMenu();
            choice = getInt();

            switch (choice) {
                case 1:
                    H.alg1(puzzle);
                    break;
                case 2:
                    H.alg2(puzzle);
                    break;
                case 3:
                    puzzle = newPuzzle();
                    puzzle.printPuzzle();
                    break;
                case 4:
                    puzzle.printPuzzle();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}

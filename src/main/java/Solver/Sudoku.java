package Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.time.Instant;
import java.time.Duration;

public class Sudoku {
    private int[][] board;
    private int N;
    public static AtomicBoolean solutionFound = new AtomicBoolean(false);
    public static AtomicReference<int[][]> solutionBoard = new AtomicReference<>(null);
    public static int numThreads = 3;
    public static Instant start;
    public static Duration duration;
    public List<String> failureMessages = new ArrayList<>();

    // Variável para armazenar o callback
    private CellUpdateCallback updateCallback;

    public Sudoku(int[][] board, int numThreads) {
        this.board = board;
        this.N = board.length;
        this.numThreads = numThreads;
    }

    public void setUpdateCallback(CellUpdateCallback callback) {
        this.updateCallback = callback;
    }

    public CellUpdateCallback getUpdateCallback() {
        return updateCallback;
    }

    public int[][] copyBoard() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, N);
        }
        return copy;
    }

    public boolean solve(int[][] board) {
        if (solutionFound.get()) return true;

        int row = -1, col = -1;
        boolean isEmpty = true;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    row = i;
                    col = j;
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) break;
        }

        if (isEmpty) {
            solutionFound.set(true);
            solutionBoard.set(board);
            Instant end = Instant.now();
            duration = Duration.between(start, end);
            System.out.println("Tempo de execução: " + duration.toSeconds() + "s");
            return true;
        }

        for (int num = 1; num <= N; num++) {
            if (solutionFound.get()) return true;
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (updateCallback != null) {
                    updateCallback.update(board, Thread.currentThread());
                }
                if (solve(board)) return true;
                board[row][col] = 0;
                if (updateCallback != null) {
                    updateCallback.update(board, Thread.currentThread());
                }
            }
        }
        return false;
    }

    public boolean isSafe(int[][] board, int row, int col, int num) {
        for (int d = 0; d < N; d++) {
            if (board[row][d] == num) return false;
        }
        for (int r = 0; r < N; r++) {
            if (board[r][col] == num) return false;
        }
        int sqrt = (int) Math.sqrt(N);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;
        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (board[r][d] == num) return false;
            }
        }
        return true;
    }

    public void solveConcurrently() {
        start = Instant.now();
        int firstRow = -1, firstCol = -1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    firstRow = i;
                    firstCol = j;
                    break;
                }
            }
        }

        if (firstRow == -1) {
            solutionFound.set(true);
            solutionBoard.set(board);
            return;
        }

        System.out.println("Empty cell found at " + firstRow + ", " + firstCol);

        int interval = (int) Math.ceil((double) N / numThreads);

        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int start = i * interval + 1;
            int end = Math.min((i + 1) * interval, N);
            int[][] boardCopy = copyBoard();
            threads[i] = new Concorrencia.SudokuThread(boardCopy, firstRow, firstCol, start, end, this);
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (solutionFound.get()) {
            board = solutionBoard.get();
            print();
        }
    }

    public void print() {
        for (int r = 0; r < board.length; r++) {
            if (r > 0 && r % Math.sqrt(board.length) == 0) {
                System.out.println("-".repeat(board.length * 2 - 1)); // Linha separadora
            }

            for (int d = 0; d < board.length; d++) {
                if (d > 0 && d % Math.sqrt(board.length) == 0) {
                    System.out.print("| "); // Separador de bloco
                }
                System.out.print(board[r][d] + " ");
            }
            System.out.println(); // Nova linha após cada linha do tabuleiro
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public static AtomicBoolean getSolutionFound() {
        return solutionFound;
    }

    public static void setSolutionFound(AtomicBoolean solutionFound) {
        Sudoku.solutionFound = solutionFound;
    }

    public static AtomicReference<int[][]> getSolutionBoard() {
        return solutionBoard;
    }

    public static void setSolutionBoard(AtomicReference<int[][]> solutionBoard) {
        Sudoku.solutionBoard = solutionBoard;
    }

    public static int getNumThreads() {
        return numThreads;
    }

    public static void setNumThreads(int numThreads) {
        Sudoku.numThreads = numThreads;
    }

    public static Instant getStart() {
        return start;
    }

    public static void setStart(Instant start) {
        Sudoku.start = start;
    }

    public static Duration getDuration() {
        return duration;
    }

    public static void setDuration(Duration duration) {
        Sudoku.duration = duration;
    }

    public List<String> getFailureLabel() {
        return failureMessages;
    }

    public void setFailureLabel(List<String> failureMessages) {
        this.failureMessages = failureMessages;
    }
}

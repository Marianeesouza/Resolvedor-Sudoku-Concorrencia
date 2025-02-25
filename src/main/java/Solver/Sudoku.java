package Solver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Sudoku {
    private int[][] board;
    private int N;
    public static AtomicBoolean solutionFound = new AtomicBoolean(false);
    public static AtomicReference<int[][]> solutionBoard = new AtomicReference<>(null);

    // Interface para o callback de atualização da UI
    public interface CellUpdateCallback {
        void update(int row, int col, int value, Thread thread);
    }

    // Variável para armazenar o callback
    private CellUpdateCallback updateCallback;

    public Sudoku(int[][] board) {
        this.board = board;
        this.N = board.length;
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
            return true;
        }

        for (int num = 1; num <= N; num++) {
            if (solutionFound.get()) return true;
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (updateCallback != null) {
                    updateCallback.update(row, col, num, Thread.currentThread());
                }
                if (solve(board)) return true;
                board[row][col] = 0;
                if (updateCallback != null) {
                    updateCallback.update(row, col, 0, Thread.currentThread());
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

        int totalNumbers = N;
        int numThreads = Math.min(totalNumbers, 3);
        int interval = (int) Math.ceil((double) totalNumbers / numThreads);

        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int start = i * interval + 1;
            int end = Math.min((i + 1) * interval, totalNumbers);
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
        }
    }
}

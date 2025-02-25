package Concorrencia;
import java.lang.Thread;
import java.lang.Override;
import Solver.Sudoku;
import Solver.Sudoku.CellUpdateCallback;

public class SudokuThread extends Thread {
    private final int[][] board;
    private final int row;
    private final int col;
    private final int rangeStart;
    private final int rangeEnd;
    private final Sudoku sudokuRef;

    public SudokuThread(int[][] board, int row, int col, int rangeStart, int rangeEnd, Sudoku sudokuRef) {
        this.board = board;
        this.row = row;
        this.col = col;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.sudokuRef = sudokuRef;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Intervalo " + rangeStart + "-" + rangeEnd);
        if (Sudoku.solutionFound.get()) return;
        for (int num = rangeStart; num <= rangeEnd; num++) {
            if (Sudoku.solutionFound.get()) break;
            if (sudokuRef.isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (sudokuRef.getUpdateCallback() != null) {
                    sudokuRef.getUpdateCallback().update(row, col, num);
                }
                if (sudokuRef.solve(board)) break;
                board[row][col] = 0;
                if (sudokuRef.getUpdateCallback() != null) {
                    sudokuRef.getUpdateCallback().update(row, col, 0);
                }
            }
        }
    }
}


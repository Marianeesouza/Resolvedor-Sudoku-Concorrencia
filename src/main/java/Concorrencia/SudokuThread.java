package Concorrencia;
import java.lang.Thread;
import java.lang.Override;
import Solver.Sudoku;
import Solver.CellUpdateCallback;
import javafx.application.Platform;

/**
 * Classe que representa uma thread para resolver o Sudoku.Cada thread
 * é responsável por resolver inicialmente um intervalo de valores para
 * a próxima célula vazia no tabuleiro. Após isso, as threads continuam
 * tentando resolver normalmente o tabuleiro.
 * Cada uma recebe uma cópia do tabuleiro e tenta resolver o Sudoku,
 * aquela que achar a solução primeiro, determina a solução do Sudoku.
 */

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
        boolean failure = true;
        if (Sudoku.solutionFound.get()) return;
        for (int num = rangeStart; num <= rangeEnd; num++) {
            if (Sudoku.solutionFound.get()) break;
            if (sudokuRef.isSafe(board, row, col, num)) {
                failure = false;
                board[row][col] = num;
                if (sudokuRef.getUpdateCallback() != null) {
                    sudokuRef.getUpdateCallback().update(board, Thread.currentThread());
                }
                if (sudokuRef.solve(board)) break;
                board[row][col] = 0;
                failure = true;
                if (sudokuRef.getUpdateCallback() != null) {
                    sudokuRef.getUpdateCallback().update(board, Thread.currentThread());
                }
            }
        }
        if (failure) {
            String failureMsg = "Falha na thread " + Thread.currentThread().getName();
            System.out.println(failureMsg);
            Platform.runLater(() -> {
                sudokuRef.getFailureLabel().add(failureMsg);
            });
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public Sudoku getSudokuRef() {
        return sudokuRef;
    }
}


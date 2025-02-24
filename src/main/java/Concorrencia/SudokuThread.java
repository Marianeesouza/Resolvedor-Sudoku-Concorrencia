package Concorrencia;
import java.lang.Thread;
import java.lang.Override;
import Solver.Sudoku;

/**
 * Thread para resolver o Sudoku.
 */

public class SudokuThread extends Thread {
    private final Sudoku sudoku;
    private final int numRangeInitial;
    private final int numRangeFinal;

    public SudokuThread(Sudoku sudoku, int numRangeInitial, int numRangeFinal) {
        this.sudoku = sudoku;
        this.numRangeInitial = numRangeInitial;
        this.numRangeFinal = numRangeFinal;
    }

    @Override
    public void run() {
        sudoku.solve();
    }
}

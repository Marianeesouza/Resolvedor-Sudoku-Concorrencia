package Solver;

// Interface para o callback de atualização da UI
public interface CellUpdateCallback {
    void update(int[][] board, Thread thread);
}


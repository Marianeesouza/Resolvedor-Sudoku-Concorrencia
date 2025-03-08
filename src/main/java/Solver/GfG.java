package Solver;

// Java Program to solve Sudoku problem
import Concorrencia.GfgThread;
import Concorrencia.SudokuThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.floor;

public class GfG {

    private volatile Cell[][] board;
    private int N; // number of columns/rows.
    private int numThreads = 1;
    private AtomicReference<Boolean> solved = new AtomicReference<>(false);

    public GfG(Cell[][] board, int numThreads) {
        this.board = board;
        this.N = board.length;
        this.numThreads = numThreads;
    }

    // Function to check if it is safe to place num at mat[row][col]
    public synchronized boolean isSafe(int row, int col, int num) {
        System.out.println(num + " em (" + row + ", " + col + ")");
        // Verifica se a linha e a coluna estão dentro dos limites do tabuleiro
        if (row < 0 || row >= N || col < 0 || col >= N) {
            System.out.println("Fora dos limites: (" + row + ", " + col + ")");
            return false;
        }

        for (int d = 0; d < N; d++) {
            if (board[row][d].getValue() == num) {
                System.out.println("Número " + num + " já existe na linha " + row);
                return false;
            }
        }

        for (int r = 0; r<N; r++) {
            if (board[r][col].getValue() == num) {
                System.out.println("Número " + num + " já existe na coluna " + col);
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(N);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (r < N && d < N && board[r][d].getValue() == num) {
                    System.out.println("Número " + num + " já existe no bloco " + boxRowStart + ", " + boxColStart);
                    return false;
                }
            }
        }
        System.out.println("Número " + num + " é seguro em (" + row + ", " + col + ")");
        return true;
    }

    // Function to solve the Sudoku problem
    public boolean solveSudokuRec(int row, int col) {

        // base case: Reached nth column of the last row
        if (row == N-1 && col == N) {
            setSolved(true);
            return true;
        }

        // If last column of the row go to the next row
        if (col == N) {
            row++;
            col = 0;
        }

        // If cell is already occupied then move forward
        if (board[row][col].getValue() != 0)
            return solveSudokuRec(row, col + 1);

        // Calculate thread ranges and create threads
        List<GfgThread> threads = new ArrayList<>();
        int step = (int) floor(N / numThreads); // Cada thread testa step valores
        for (int i = 1; i <= N; i += step) {
            int start = i;
            int end = i + step - 1;
            System.out.println("Thread " + start + " - " + end);
            // Cria uma thread para cada intervalo e adiciona na lista
            threads.add(new GfgThread(this, start, end, row, col));
        }

        System.out.println("Criando " + threads.size() + " threads para resolver a célula [" + row + ", " + col + "]");

        List<FutureTask<Boolean>> futureTasks = new ArrayList<>();
        for (GfgThread thread : threads) {
            FutureTask<Boolean> futureTask = new FutureTask<>(thread);
            futureTasks.add(futureTask);
        }

        for (FutureTask<Boolean> futureTask : futureTasks) {
            Thread thread = new Thread(futureTask);
            thread.start();
        }

        for (FutureTask<Boolean> futureTask : futureTasks) {
            try {
                if (futureTask.get()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void solveSudoku() {
        solveSudokuRec(0, 0);
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public synchronized Cell getCell(int row, int col) {
        return board[row][col];
    }

    public synchronized void setCell(int row, int col, int value) {
        board[row][col].setValue(value);
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public synchronized boolean isSolved() {
        return solved.get();
    }

    public synchronized void setSolved(boolean solved) {
        this.solved.set(solved);
    }

    public static void main(String[] args) {
//        Cell[][] mat = {
//                {new Cell(0), new Cell(4), new Cell(2), new Cell(0)},
//                {new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
//                {new Cell(1), new Cell(0), new Cell(4), new Cell(0)},
//                {new Cell(0), new Cell(0), new Cell(0), new Cell(0)}
//        };

        Cell[][] mat = {
                {new Cell(9), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(2), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(1), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)},
                {new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0), new Cell(0)}
        };

        GfG g = new GfG(mat, 1);
        g.solveSudoku();

        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++)
                System.out.print(mat[i][j].getValue() + " ");
            System.out.println();
        }
    }
}

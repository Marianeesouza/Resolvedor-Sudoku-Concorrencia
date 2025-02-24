package Solver;

import Concorrencia.SudokuThread;

import static com.almasb.fxgl.core.math.FXGLMath.floor;

public class Sudoku {
    private int[][] board;
    private int N; // number of columns/rows.

    public Sudoku(int[][] board) {
        this.board = board;
        this.N = board.length;
    }

    public boolean solve() {
        int row = -1;
        int col = -1;
        boolean isEmpty = true;

        // Verifica há células vazias no tabuleiro
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    row = i;
                    col = j;

                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                break;
            }
        }

        if (isEmpty) {
            return true;
        }

        // Tenta preencher a célula vazia com um número válido
        for (int num = 1; num <= N; num++) {
            if (isSafe(row, col, num)) {
                board[row][col] = num;
                if (solve()) {
                    return true;
                } else {
                    board[row][col] = 0;
                }
            }
        }

        return false;
    }

    private boolean isSafe(int row, int col, int num) {
        for (int d = 0; d < N; d++) {
            if (board[row][d] == num) {
                return false;
            }
        }

        for (int[] ints : board) {
            if (ints[col] == num) {
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(N);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (board[r][d] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    public void print() {
        for (int r = 0; r < N; r++) {
            if (r > 0 && r % Math.sqrt(N) == 0) {
                System.out.println("-".repeat(N * 2 - 1)); // Linha separadora
            }

            for (int d = 0; d < N; d++) {
                if (d > 0 && d % Math.sqrt(N) == 0) {
                    System.out.print("| "); // Separador de bloco
                }
                System.out.print(board[r][d] + " ");
            }
            System.out.println(); // Nova linha após cada linha do tabuleiro
        }
    }


    public static void main(String[] args) {
//        int[][] board = new int[][] {
//            {3, 0, 6, 5, 0, 8, 4, 0, 0},
//            {5, 2, 0, 0, 0, 0, 0, 0, 0},
//            {0, 8, 7, 0, 0, 0, 0, 3, 1},
//            {0, 0, 3, 0, 0, 0, 1, 8, 0},
//            {9, 0, 0, 8, 6, 3, 0, 0, 5},
//            {0, 5, 0, 0, 9, 0, 6, 0, 0},
//            {1, 3, 0, 0, 0, 0, 2, 5, 0},
//            {0, 0, 0, 0, 0, 0, 0, 7, 4},
//            {0, 0, 5, 2, 0, 6, 3, 0, 0}
//        };

        int[][] board = {
                { 1,  0,  0,  4,  0,  6,  7,  0,  9,  0, 11,  0, 13, 14,  0, 16 },
                { 5,  0,  0,  0,  9,  0, 11, 12,  0, 14, 15,  0,  1,  0,  3,  0  },
                { 0, 10, 11,  0, 13,  0,  0, 16,  1,  0,  3,  4,  5,  0,  7,  8  },
                {13, 14,  0,  0,  1,  0,  3,  4,  5,  0,  7,  0,  9,  0, 11,  0 },

                { 2,  1,  0,  0,  6,  5,  0,  0, 10,  9,  0, 11, 14,  0,  0, 15 },
                { 0,  5,  0,  7, 10,  0, 12,  0, 14,  0,  0, 15,  2,  0,  4,  3  },
                {10,  0, 12, 11, 14, 13,  0,  0,  2,  1,  4,  0,  0,  5,  8,  0  },
                {14,  0, 16,  0,  0,  1,  4,  3,  0,  5,  0,  7, 10,  9,  0, 11 },

                { 0,  4,  1,  2,  7,  8,  0,  6,  0,  0,  9, 10,  0, 16,  0, 14 },
                { 7,  0,  5,  6, 11,  0,  9, 10, 15,  0,  0, 14,  3,  0,  1,  0  },
                {11, 12,  9,  0, 15,  0,  0, 14,  3,  4,  1,  0,  7,  0,  0,  6  },
                {15,  0,  0, 14,  3,  4,  1,  0,  7,  8,  0,  6, 11, 12,  9,  0 },

                { 4,  0,  0,  1,  8,  0,  6,  5, 12,  0, 10,  0, 16,  0, 14,  0 },
                { 0,  7,  0,  5, 12,  0,  0,  9,  0, 15,  0, 13,  4,  3,  0,  1  },
                {12,  0, 10,  0, 16, 15, 14,  0,  0,  3,  2,  1,  8,  7,  0,  5  },
                {16,  0,  0, 13,  4,  3,  0,  1,  8,  7,  6,  0, 12, 11,  0,  0  }
        };

//        int[][] board = {
//                { 0,  0,  0,  0,  0,  6,  0,  0,  0, 10,  0,  0,  0,  0,  0, 16 },
//                { 0,  0,  0,  0,  9,  0,  0, 12,  0,  0,  0,  0,  0, 15,  0,  0 },
//                { 0,  0,  0,  0,  0,  0,  0, 16,  0,  0,  3,  0,  5,  0,  0,  0 },
//                {13,  0,  0,  0,  0,  0,  0,  4,  0,  0,  0,  0,  9,  0,  0,  0 },
//
//                { 0,  1,  0,  0,  0,  5,  0,  0, 10,  0,  0,  0,  0,  0,  0, 15 },
//                { 0,  0,  0,  7,  0,  0, 12,  0,  0,  0,  0,  0,  2,  0,  0,  0 },
//                { 0,  0, 12,  0, 14,  0,  0,  0,  0,  0,  4,  0,  0,  5,  0,  0 },
//                {14,  0,  0,  0,  0,  0,  4,  3,  0,  0,  0,  7,  0,  9,  0,  0 },
//
//                { 0,  0,  1,  2,  0,  0,  0,  6,  0,  0,  0,  0,  0, 16,  0,  0 },
//                { 7,  0,  0,  6,  0,  0,  0, 10, 15,  0,  0,  0,  0,  0,  1,  0 },
//                {11,  0,  0,  0, 15,  0,  0, 14,  0,  4,  0,  0,  7,  0,  0,  6 },
//                { 0,  0,  0, 14,  0,  4,  1,  0,  7,  0,  0,  6, 11,  0,  0,  0 },
//
//                { 4,  0,  0,  0,  8,  0,  0,  5,  0,  0, 10,  0,  0,  0, 14,  0 },
//                { 0,  0,  0,  5,  0,  0,  0,  9,  0,  0,  0, 13,  4,  3,  0,  0 },
//                {12,  0,  0,  0,  0,  0, 14,  0,  0,  0,  0,  1,  8,  0,  0,  5 },
//                { 0,  0,  0, 13,  4,  3,  0,  0,  8,  7,  0,  0, 12,  0,  0,  0 }
//        };




        Sudoku sudoku = new Sudoku(board);
        if (sudoku.solve()) {
            sudoku.print();
        } else {
            System.out.println("No solution");
        }
    }
}

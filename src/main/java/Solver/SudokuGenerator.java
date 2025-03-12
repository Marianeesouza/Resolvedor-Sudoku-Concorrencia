package Solver;

import java.util.*;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private int[][] board = new int[SIZE][SIZE];

    public int[][] generateBoard(int emptyCells) {
        fillBoard();
        removeNumbers(emptyCells);
        return board;
    }

    private void fillBoard() {
        // Preenche o tabuleiro vazio com zeros
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(board[i], 0);
        }
        solveBoard(0, 0); // Usa backtracking para preencher o Sudoku
    }

    private boolean solveBoard(int row, int col) {
        if (row == SIZE) return true; // Tabuleiro preenchido
        if (col == SIZE) return solveBoard(row + 1, 0); // Vai para próxima linha
        if (board[row][col] != 0) return solveBoard(row, col + 1); // Se já estiver preenchido, avança

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) numbers.add(i);
        Collections.shuffle(numbers); // Embaralha para aleatorizar o preenchimento

        for (int num : numbers) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (solveBoard(row, col + 1)) return true;
                board[row][col] = 0; // Backtracking
            }
        }
        return false;
    }

    private boolean isValid(int row, int col, int num) {
        // Verifica se o número já existe na linha, coluna ou subgrade 3x3
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) return false;
        }

        int startRow = (row / SUBGRID) * SUBGRID;
        int startCol = (col / SUBGRID) * SUBGRID;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                if (board[startRow + i][startCol + j] == num) return false;
            }
        }
        return true;
    }

    private void removeNumbers(int emptyCells) {
        Random rand = new Random();
        int removed = 0;
        while (removed < emptyCells) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (board[row][col] != 0) {
                int temp = board[row][col];
                board[row][col] = 0;

                // Garante que ainda há uma solução única
                if (!hasUniqueSolution()) {
                    board[row][col] = temp; // Restaura o número se houver múltiplas soluções
                } else {
                    removed++;
                }
            }
        }
    }

    private boolean hasUniqueSolution() {
        int[][] tempBoard = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            tempBoard[i] = Arrays.copyOf(board[i], SIZE);
        }
        return countSolutions(tempBoard, 0, 0) == 1;
    }

    private int countSolutions(int[][] tempBoard, int row, int col) {
        if (row == SIZE) return 1;
        if (col == SIZE) return countSolutions(tempBoard, row + 1, 0);
        if (tempBoard[row][col] != 0) return countSolutions(tempBoard, row, col + 1);

        int count = 0;
        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num)) {
                tempBoard[row][col] = num;
                count += countSolutions(tempBoard, row, col + 1);
                if (count > 1) return count; // Se houver mais de uma solução, já retorna
                tempBoard[row][col] = 0; // Backtracking
            }
        }
        return count;
    }
}

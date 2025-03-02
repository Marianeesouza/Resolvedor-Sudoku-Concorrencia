package Solver;

import java.util.Random;

public class Sudoku {
    private int[][] board;
    private final int N;
    private final int sqrtN;
    private static final Random rand = new Random(System.currentTimeMillis());

    public Sudoku(int[][] board) {
        this.board = board;
        this.N = board.length;
        this.sqrtN = (int) Math.sqrt(N);
    }

    public boolean solve() {
        int[] emptyCell = findEmptyCell();
        if (emptyCell == null) {
            return true; // Solução encontrada
        }

        int row = emptyCell[0], col = emptyCell[1];

        for (int num = 1; num <= N; num++) {
            if (isSafe(row, col, num)) {
                board[row][col] = num;
                if (solve()) {
                    return true;
                }
                board[row][col] = 0; // Backtrack
            }
        }
        return false; // Nenhuma solução válida
    }

    private int[] findEmptyCell() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public int[][] getBoard() {
        return board;
    }

    private boolean isSafe(int row, int col, int num) {
        for (int i = 0; i < N; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int boxRowStart = row - row % sqrtN;
        int boxColStart = col - col % sqrtN;

        for (int r = 0; r < sqrtN; r++) {
            for (int c = 0; c < sqrtN; c++) {
                if (board[boxRowStart + r][boxColStart + c] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public void generateBoard() {
        this.board = new int[N][N];

        // Preenche o tabuleiro com uma solução válida
        fillBoard();

        // Remove números aleatoriamente para criar um quebra-cabeça
        removeNumbers();
    }

    private void fillBoard() {
        if (!solve()) {
            throw new IllegalStateException("Não foi possível gerar uma solução para o Sudoku.");
        }
    }

    private void removeNumbers() {
        int count = N * N / 2; // Número de células a serem removidas (ajuste conforme necessário)
        while (count > 0) {
            int row = rand.nextInt(N);  // Aleatório
            int col = rand.nextInt(N);  // Aleatório
            if (board[row][col] != 0) {
                board[row][col] = 0;  // Remover número
                count--;
            }
        }
    }

    public void print() {
        for (int i = 0; i < N; i++) {
            if (i % sqrtN == 0 && i != 0) {
                System.out.println("-".repeat(N * 2));
            }
            for (int j = 0; j < N; j++) {
                if (j % sqrtN == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print((board[i][j] == 0 ? "." : board[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku(new int[9][9]);
        sudoku.generateBoard();
        sudoku.print();
        System.out.println("\nResolvendo...");
        sudoku.solve();
        sudoku.print();
    }
}

package Concorrencia;
import java.lang.Thread;
import java.lang.Override;
<<<<<<< Updated upstream
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

=======
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import Exceptions.SudokuSolverException;
>>>>>>> Stashed changes
import Solver.Sudoku;

import static java.lang.Math.floor;

/**
 * Thread para resolver o Sudoku. A thread é chamada no ínicio
 * da tentativa de resolver o tabuleiro e cria outras threads.
 * É por meio dela que ocorre a divisão do trabalho e o "backtracking".
 * A thread não carrega uma cópia do tabuleiro, mas sim acessa diretamente
 * o tabuleiro original de forma síncrona, o range que ela vai testar,
 * a referência da célula que ela vai testar e deve poder
 * criar outras threads a partir dela.
 */

<<<<<<< Updated upstream
public class SudokuThread extends Thread {
    /**
     * A Thread recebe uma cópia do tabuleiro, a referência do Sudoku,
     * a linha e a coluna da célula a ser preenchida, o intervalo inicial
     * e final de números a serem testados e o ExecutorService do Sudoku.
     */
    private final int[][] boardcopy;
    private final Sudoku refSudoku;
    private final int row;
    private final int col;
    private final ExecutorService executor;
=======
public class SudokuThread implements Callable<Boolean> {
    private Sudoku sudoku;
>>>>>>> Stashed changes
    private final int numRangeInitial;
    private final int numRangeFinal;
    private final int linha;
    private final int coluna;

<<<<<<< Updated upstream
    public SudokuThread(Sudoku sudoku, int numRangeInitial, int numRangeFinal,
                        int[][] boardcopy, int row, int col, ExecutorService executor) {
        this.refSudoku = sudoku;
        this.numRangeInitial = numRangeInitial;
        this.numRangeFinal = numRangeFinal;
        this.boardcopy = boardcopy;
        this.row = row;
        this.col = col;
        this.executor = executor;
=======
    public SudokuThread(Sudoku sudoku, int numRangeInitial, int numRangeFinal, int linha, int coluna) {
        this.sudoku = sudoku;
        this.numRangeInitial = numRangeInitial;
        this.numRangeFinal = numRangeFinal;
        this.linha = linha;
        this.coluna = coluna;
>>>>>>> Stashed changes
    }

    /**
     * Método run da Thread.
     * Tenta preencher a célula vazia com um número válido.
     */
    @Override
<<<<<<< Updated upstream
    public void run() {
        // Primeiro verifica se a solução já foi encontrada
        if (refSudoku.getSolved().get()) {
            return;
        }

        // Se o número da célula é igual ao tamanho do tabuleiro, o algoritmo passou por todas as células
        if (row == boardcopy.length) {
            // Verifica se o tabuleiro já foi completamente preenchido e se é válido
            if (isBoardFilled() && isBoardValid()) {
                // Solução encontrada
                if (refSudoku.getSolution().get() == null) {
                    refSudoku.setSolution(new AtomicReference<>(boardcopy));
                    refSudoku.setSolved(new AtomicBoolean(true));
                    executor.shutdownNow();
                }
            }
        }

        // Calcula a próxima linha e coluna
        int nextRow = (col == boardcopy.length - 1) ? row + 1 : row;    // Se a coluna é a última, avança para a próxima linha
        int nextCol = (col == boardcopy.length - 1) ? 0 : col + 1;      // Se a coluna é a última, a próxima coluna é a primeira

        // Se a célula já está preenchida, avança para a próxima
        if (boardcopy[row][col] != 0) {
            executor.submit(new SudokuThread(refSudoku, numRangeInitial, numRangeFinal, boardcopy, nextRow, nextCol, executor));
            return;
        }

        // Tenta preencher a célula com um número válido
        for (int num = numRangeInitial; num <= numRangeFinal; num++) {
            if (isSafe(row, col, num)) {
                boardcopy[row][col] = num;

                // Caso seja possível preencher a célula, a thread chama recursivamente outras threads para preencher as próximas células
                // Cada thread testará um intervalo de números, então aqui são calculados os novos intervalos
                // O número de intervalos depende do que o usuário escolheu, que é numThreads no Sudoku
                int step = refSudoku.getNumThreads();
                for (int i = 0; i <= refSudoku.getN(); i+=step) {
                    int start = i;
                    int end = i + step - 1;
                    executor.submit(new SudokuThread(refSudoku, start, end, copyBoard(boardcopy), nextRow, nextCol, executor));
                }
                try {
                    executor.awaitTermination(10, java.util.concurrent.TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean isSafe(int row, int col, int num) {
        for (int i = 0; i < boardcopy.length; i++) {
            if (boardcopy[row][i] == num || boardcopy[i][col] == num) {
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(boardcopy.length);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;
        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int c = boxColStart; c < boxColStart + sqrt; c++) {
                if (boardcopy[r][c] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    private boolean isBoardFilled() {
        for (int i = 0; i < boardcopy.length; i++) {
            for (int j = 0; j < boardcopy[i].length; j++) {
                if (boardcopy[i][j] == 0) {
                    return false; // Ainda há células vazias
                }
            }
        }
        return true; // Todas as células estão preenchidas
    }

    private boolean isBoardValid() {
        for (int i = 0; i < boardcopy.length; i++) {
            for (int j = 0; j < boardcopy[i].length; j++) {
                if (boardcopy[i][j] != 0) {
                    int num = boardcopy[i][j];
                    boardcopy[i][j] = 0; // Temporariamente remove o número para verificar conflitos
                    if (!isSafe(i, j, num)) {
                        boardcopy[i][j] = num; // Restaura o número
                        return false; // Conflito encontrado
                    }
                    boardcopy[i][j] = num; // Restaura o número
                }
            }
        }
        return true; // Tabuleiro válido
=======
    public Boolean call() throws Exception {
        System.out.println("Thread iniciada");
        // Testa se o tabuleiro foi resolvido por outra thread
        if (sudoku.isSolved()) {
            return true;
        }

        // Se o número da linha for igual ao tamanho do tabuleiro, o tabuleiro foi resolvido
        if (linha == sudoku.getN()) {
            sudoku.setEnd(Instant.now());
            sudoku.setSolved(true);
            System.out.println("Sudoku resolvido em " + Duration.between(sudoku.getStart(), sudoku.getEnd()).toMillis() + "ms");
            return true;
        }

        // Testa se a célula é vazia
        if (sudoku.getCell(linha, coluna) == 0) {
            // Testa os números possíveis para a célula de acordo com o range
            System.out.println("Testando célula [" + linha + "][" + coluna + "]");
            for (int num = numRangeInitial; num <= numRangeFinal; num++) {
                System.out.println("Testando número " + num);
                if (sudoku.isSafe(linha, coluna, num)) {
                    // Se o número for seguro, ele é adicionado à copia do tabuleiro
                    sudoku.setCell(linha, coluna, num);

                    }
                    if (sudoku.getCell(linha, coluna) == num) {
                        sudoku.setCell(linha, coluna, 0);
                    }
                }
            }
            return false;
        }
        // Se a célula não for vazia, apenas passa para a próxima célula
        // e cria as threads para testar esse célula
        // E outras threads são criadas a partir daqui
>>>>>>> Stashed changes
    }


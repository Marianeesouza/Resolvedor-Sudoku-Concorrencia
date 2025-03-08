package Solver;

import Concorrencia.SudokuThread;

<<<<<<< Updated upstream
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.almasb.fxgl.core.math.FXGLMath.floor;
=======
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.time.Duration;
import java.time.Instant;

import static java.lang.Math.floor;

/**
 * A classe Sudoku é responsável por criar e resolver um tabuleiro de Sudoku.
 * As threads iniciais são criadas aqui. A partir delas, novas threads são criadas
 * para resolver o tabuleiro.
 * A base para a resolução do Sudoku é um algoritmo de backtracking.
 * As tentativas de preencher as células são divididas entre as threads.
 *
 * O Sudoku tem um tabuleiro NxN inicial (que depois será substituido pelo tabuleiro resolvido),
 * um ServiceExecutor para criar as threads, o número de threads que serão criadas por vez,
 * um boolean que determina se o tabuleiro foi resolvido, e um contador de tempo para medir
 * o tempo de execução.
 */
>>>>>>> Stashed changes

public class Sudoku {
    private int[][] board;
    private int N; // number of columns/rows.
<<<<<<< Updated upstream
    private int numThreads;
    private ExecutorService executor;
    private AtomicReference<int[][]> solution;
    private AtomicBoolean solved = new AtomicBoolean(false);
=======
    private int numThreads = 1;
    private volatile boolean solved = false;
    private Instant start;
    private Instant end;
    private ExecutorService executor;

>>>>>>> Stashed changes

    public Sudoku(int[][] board) {
        this.board = board;
        this.N = board.length;
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

<<<<<<< Updated upstream
    /**
     * Método inicial para resolver o Sudoku.
     * Procura a primeira célula vazia e divide a resolução dela em partes iguais para as threads.
     * @return true se o Sudoku foi resolvido, false caso contrário.
     */
    public boolean solve(int numThreads) {

        this.setExecutor(Executors.newCachedThreadPool());
        this.setNumThreads(numThreads);
=======
    public Sudoku(int[][] board, int numThreads) {
        this.board = board;
        this.N = board.length;
        this.numThreads = numThreads;
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    public boolean solve() throws InterruptedException {
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

        // Se não há células vazias, o tabuleiro já está resolvido
        if (isEmpty) {
            setSolution(new AtomicReference<>(board));
            setSolved(new AtomicBoolean(true));
            return true;
        }

        // Divide o intervalo do tabuleiro em partes (numThreads) iguais para as threads
        int step = (int) floor(N / numThreads); // Cada thread testa step valores
        for (int i = 1; i <= N; i += step) {
            int start = i;
            int end = i + step - 1;
            int[][] boardCopy = copyBoard(board);
            // Cria uma thread para cada intervalo
            executor.submit(new SudokuThread(this, start, end, boardCopy, row, col, executor));
        }

        try {
            executor.awaitTermination(10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (solved.get()) {
            return true;
=======
        // Se não há células vazias, o tabuleiro está resolvido
        if (isEmpty) {
            this.solved = true;
            this.end = Instant.now();
            System.out.println("Tempo de execução: " + Duration.between(start, end).toMillis() + "ms");
            return true;
        }

        // Se ainda há células a serem preenchidas, cria as threads
        // que vão tentar preencher as células.

        List<SudokuThread> threads = createThreads(col, row); // Cria as threads a partir da primeira célula vazia
        //Depois que todas as threads são criadas, executa cada uma e espera o resultado
        List<Future<Boolean>> resultados = executor.invokeAll(threads);

        for (Future<Boolean> resultado : resultados) {
            try {
                if (resultado.get()) {
                    this.shutdownExecutor();
                    this.solved = true;
                    this.end = Instant.now();
                    System.out.println("Tempo de execução: " + Duration.between(start, end).toMillis() + "ms");
                    return true;
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
>>>>>>> Stashed changes
        }

        return false;
    }

    public synchronized boolean isSafe(int row, int col, int num) {
        System.out.println(num + " em (" + row + ", " + col + ")");
        // Verifica se a linha e a coluna estão dentro dos limites do tabuleiro
        if (row < 0 || row >= N || col < 0 || col >= N) {
            System.out.println("Fora dos limites: (" + row + ", " + col + ")");
            return false;
        }

        for (int d = 0; d < N; d++) {
            if (board[row][d] == num) {
                System.out.println("Número " + num + " já existe na linha " + row);
                return false;
            }
        }

        for (int r = 0; r<N; r++) {
            if (board[r][col] == num) {
                System.out.println("Número " + num + " já existe na coluna " + col);
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(N);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (r < N && d < N && board[r][d] == num) {
                    System.out.println("Número " + num + " já existe no bloco " + boxRowStart + ", " + boxColStart);
                    return false;
                }
            }
        }
        System.out.println("Número " + num + " é seguro em (" + row + ", " + col + ")");
        return true;
    }

<<<<<<< Updated upstream
    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
=======
    public List<SudokuThread> createThreads (int coluna, int linha) {
        List<SudokuThread> threads = new ArrayList<>();
        int step = (int) floor(N / numThreads); // Cada thread testa step valores
        for (int i = 1; i <= N; i += step) {
            int start = i;
            int end = i + step - 1;
            System.out.println("Thread " + start + " - " + end);
            // Cria uma thread para cada intervalo e adiciona na lista
            threads.add(new SudokuThread(this, start, end, linha, coluna));
        }
        System.out.println("Criando " + threads.size() + " threads para resolver a célula [" + linha + ", " + coluna + "]");
        return threads;
    }

    public void shutdownExecutor() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public synchronized void setCell(int row, int col, int value) {
        board[row][col] = value;
    }

    public synchronized int getCell(int row, int col) {
        return board[row][col];
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

    public synchronized void setSolved(boolean solved) {
        this.solved = solved;
    }
    public synchronized boolean isSolved() {
        return solved;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
>>>>>>> Stashed changes
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


<<<<<<< Updated upstream
    public static void main(String[] args) {
        int[][] board = new int[][] {
            {3, 0, 6, 5, 0, 8, 4, 0, 0},
            {5, 2, 0, 0, 0, 0, 0, 0, 0},
            {0, 8, 7, 0, 0, 0, 0, 3, 1},
            {0, 0, 3, 0, 0, 0, 1, 8, 0},
            {9, 0, 0, 8, 6, 3, 0, 0, 5},
            {0, 5, 0, 0, 9, 0, 6, 0, 0},
            {1, 3, 0, 0, 0, 0, 2, 5, 0},
            {0, 0, 0, 0, 0, 0, 0, 7, 4},
            {0, 0, 5, 2, 0, 6, 3, 0, 0}
        };

//        int[][] board = {
//                { 1,  0,  0,  4,  0,  6,  7,  0,  9,  0, 11,  0, 13, 14,  0, 16 },
//                { 5,  0,  0,  0,  9,  0, 11, 12,  0, 14, 15,  0,  1,  0,  3,  0  },
//                { 0, 10, 11,  0, 13,  0,  0, 16,  1,  0,  3,  4,  5,  0,  7,  8  },
//                {13, 14,  0,  0,  1,  0,  3,  4,  5,  0,  7,  0,  9,  0, 11,  0 },
//
//                { 2,  1,  0,  0,  6,  5,  0,  0, 10,  9,  0, 11, 14,  0,  0, 15 },
//                { 0,  5,  0,  7, 10,  0, 12,  0, 14,  0,  0, 15,  2,  0,  4,  3  },
//                {10,  0, 12, 11, 14, 13,  0,  0,  2,  1,  4,  0,  0,  5,  8,  0  },
//                {14,  0, 16,  0,  0,  1,  4,  3,  0,  5,  0,  7, 10,  9,  0, 11 },
//
//                { 0,  4,  1,  2,  7,  8,  0,  6,  0,  0,  9, 10,  0, 16,  0, 14 },
//                { 7,  0,  5,  6, 11,  0,  9, 10, 15,  0,  0, 14,  3,  0,  1,  0  },
//                {11, 12,  9,  0, 15,  0,  0, 14,  3,  4,  1,  0,  7,  0,  0,  6  },
//                {15,  0,  0, 14,  3,  4,  1,  0,  7,  8,  0,  6, 11, 12,  9,  0 },
//
//                { 4,  0,  0,  1,  8,  0,  6,  5, 12,  0, 10,  0, 16,  0, 14,  0 },
//                { 0,  7,  0,  5, 12,  0,  0,  9,  0, 15,  0, 13,  4,  3,  0,  1  },
//                {12,  0, 10,  0, 16, 15, 14,  0,  0,  3,  2,  1,  8,  7,  0,  5  },
//                {16,  0,  0, 13,  4,  3,  0,  1,  8,  7,  6,  0, 12, 11,  0,  0  }
//        };

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




=======
    public static void main(String[] args) throws InterruptedException {
//        int[][] board = new int[][]{
//                {3, 0, 6, 5, 0, 8, 4, 0, 0},
//                {5, 2, 0, 0, 0, 0, 0, 0, 0},
//                {0, 8, 7, 0, 0, 0, 0, 3, 1},
//                {0, 0, 3, 0, 0, 0, 1, 8, 0},
//                {9, 0, 0, 8, 6, 3, 0, 0, 5},
//                {0, 5, 0, 0, 9, 0, 6, 0, 0},
//                {1, 3, 0, 0, 0, 0, 2, 5, 0},
//                {0, 0, 0, 0, 0, 0, 0, 7, 4},
//                {0, 0, 5, 2, 0, 6, 3, 0, 0}
//        };

        int[][] board = new int[][]{
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };

>>>>>>> Stashed changes
        Sudoku sudoku = new Sudoku(board);
        if (sudoku.solve(3)) {
            sudoku.print();
        } else {
            System.out.println("No solution");
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
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

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public AtomicReference<int[][]> getSolution() {
        return solution;
    }

    public void setSolution(AtomicReference<int[][]> solution) {
        this.solution = solution;
    }

    public AtomicBoolean getSolved() {
        return solved;
    }

    public void setSolved(AtomicBoolean solved) {
        this.solved = solved;
    }
}

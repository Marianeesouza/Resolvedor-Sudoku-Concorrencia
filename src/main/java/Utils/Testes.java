package Utils;

import Solver.Sudoku;
import Solver.SudokuGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

public class Testes {
    public static void main(String[] args) {
        // Testes para o Sudoku 16x16
        for (int i = 1; i<=13; i++){
            for (int j : new int[]{2, 3, 4, 8, 16}) {
                int [][] board = new SudokuGenerator(16).generateBoard(i*10);
                for (int k = 0; k < 5; k++) {
                    Sudoku sudoku = new Sudoku(board, j);
                    sudoku.solveConcurrently();
                    salvarResultadosSudoku(sudoku.numThreads, sudoku.duration, i*10, 16);
                }
            }
        }
    }

    private static void salvarResultadosSudoku(int numThreads, Duration tempoExecucao, int espacos, int size) {
        String nomeArquivo = "sudoku_resultados.csv";
        boolean arquivoExiste = new File(nomeArquivo).exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            if (!arquivoExiste) {
                writer.write("Threads,Tempo (ms),Espaços,Tamanho\n");
            }
            writer.write(numThreads + "," + tempoExecucao.toMillis() + "," + espacos + "," + size);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao salvar os resultados do Sudoku: " + e.getMessage());
        }
    }
}

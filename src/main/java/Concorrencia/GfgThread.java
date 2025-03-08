package Concorrencia;

import Solver.GfG;
import java.util.concurrent.Callable;

public class GfgThread implements Callable<Boolean> {
    private GfG gfg;
    private int inicialRange;
    private int finalRange;
    private int linha;
    private int coluna;

    public GfgThread(GfG gfg, int inicialRange, int finalRange, int linha, int coluna) {
        this.gfg = gfg;
        this.inicialRange = inicialRange;
        this.finalRange = finalRange;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Thread testando números de " + inicialRange + " a " + finalRange + " na célula (" + linha + ", " + coluna + ")");

        for (int num = inicialRange; num <= finalRange; num++) {
            // Se outra thread já encontrou a solução, interrompe a execução
            if (gfg.getCell(linha, coluna).isSolved()) {
                System.out.println("Thread abortando porque outra encontrou a solução.");
                return false;
            }

            if (gfg.isSafe(linha, coluna, num)) {
                synchronized (gfg) { // Garante que só uma thread pode modificar a célula por vez
                    if (gfg.getCell(linha, coluna).isSolved()) {
                        System.out.println("Thread abortando porque outra encontrou a solução.");
                        return false; // Outra thread já resolveu a célula enquanto essa estava esperando o lock
                    }

                    gfg.setCell(linha, coluna, num);
                    System.out.println("Thread encontrou solução: " + num + " na célula (" + linha + ", " + coluna + ")");
                    gfg.getCell(linha, coluna).setIsSolved(true); // Marca a célula como resolvida
                }
                if (gfg.solveSudokuRec(linha, coluna)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetSolved() {
        gfg.getCell(linha, coluna).setIsSolved(false);
    }
}
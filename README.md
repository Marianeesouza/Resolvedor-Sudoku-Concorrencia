# Sudoku Solver Paralelo

Este projeto implementa um solucionador de Sudoku utilizando **backtracking** e **execução paralela com threads** para otimizar a resolução.

## Estrutura do Código

O código se baseia inicialmente em um algoritmo de **backtracking básico**, onde o tabuleiro é percorrido célula por célula e os valores possíveis são testados recursivamente:

```java
public boolean solve() {
    int row = -1, col = -1;
    boolean isEmpty = true;

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            if (board[i][j] == 0) {
                row = i;
                col = j;
                isEmpty = false;
                break;
            }
        }
        if (!isEmpty) break;
    }

    if (isEmpty) return true;

    for (int num = 1; num <= N; num++) {
        if (isSafe(row, col, num)) {
            board[row][col] = num;
            if (solve()) return true;
            board[row][col] = 0;
        }
    }
    return false;
}
```

## Como Compilar e Executar

- O projeto é escrito em **Java 22**, então é necessário ter o **JDK 22** instalado.
- Para compilar:
  ```sh
  javac SudokuSolver.java
  ```
- Para rodar:
  ```sh
  java SudokuSolver
  ```

## Paralelismo e Threads

A solução utiliza **threads** para paralelizar a resolução do Sudoku:
- Cada thread tenta resolver a **primeira célula vazia** utilizando um intervalo diferente de valores iniciais.
- Após isso, a resolução segue com **backtracking**.
- Isso permite uma abordagem **mais eficiente**, pois diferentes threads começam com valores distintos, aumentando a chance de encontrar rapidamente um caminho correto.

## Recursos Compartilhados

As threads colaboram e competem para modificar e acessar variáveis globais:

### Compartilhados por Colaboração:
- **`solutionBoard (int[][])`**: Armazena o tabuleiro resolvido. A primeira thread que encontrar a solução grava neste recurso.
- **`solutionFound (volatile boolean)`**: Indica se a solução já foi encontrada, evitando trabalho desnecessário.

### Compartilhados por Disputa:
- **`failureMessages (List<String>)`**: Threads adicionam mensagens de erro ao tentar resolver o Sudoku.
- **`updateCallback (CellUpdateCallback)`**: Interface para atualizar visualmente o tabuleiro (em caso de UI).

## Mecanismos de Sincronização Utilizados

### Exclusão Mútua (Mutex) com `synchronized`
- Métodos que acessam `solutionFound` e `solutionBoard` são **synchronized**, garantindo que apenas uma thread por vez possa modificá-los.

### Garantia de Visibilidade com `volatile`
- `solutionFound` é **volatile**, garantindo que todas as threads enxerguem seu valor atualizado imediatamente.

### Sincronização com `Platform.runLater()` (JavaFX)
- Se houver uma interface gráfica, `Platform.runLater()` é usado para garantir que a atualização da UI ocorra na **JavaFX Application Thread**, evitando condições de corrida.

## Parametrização

O sistema permite configurar:
- **Número de threads** usadas na resolução.
- **Tamanho do Sudoku** a ser resolvido.
- **Quantidade de Espaços Vazios** a serem preenchidos.

## Uso de IA Generativa

A IA generativa foi utilizada para:
- Otimizar o código e corrigir erros.
- Melhorar a organização e explicação dos conceitos.

---

**Autores**: Mariane Souza, Paulo Miguel, Gabriel Salgado, Tiago Jacinto


package Solver;

import java.util.concurrent.atomic.AtomicBoolean;

public class Cell {
    private int value;
    private AtomicBoolean isSolved = new AtomicBoolean(false);

    public Cell(int value) {
        this.value = value;
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    public synchronized boolean isSolved() {
        return isSolved.get();
    }

    public synchronized void setIsSolved(boolean value) {
        isSolved.set(value);
    }
}

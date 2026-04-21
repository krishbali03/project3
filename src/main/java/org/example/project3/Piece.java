package org.example.project3;

import java.io.Serializable;
public class Piece implements Serializable {
    public enum Color { RED, BLACK }
    Color color;
    boolean isKing; //s/o nickEh30
    public Piece(Color color) {
        this.color = color;
        this.isKing = false;
    }
}
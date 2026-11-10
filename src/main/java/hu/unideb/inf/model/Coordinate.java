package hu.unideb.inf.model;

public record Coordinate(int row, int col) {
    public Coordinate translate(int dRow, int dCol) {
        return new Coordinate(row + dRow, col + dCol);
    }
}
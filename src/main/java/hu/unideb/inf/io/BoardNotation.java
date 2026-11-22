package hu.unideb.inf.io;

import hu.unideb.inf.model.Coordinate;

import java.util.Locale;
import java.util.Optional;

public final class BoardNotation {

    private BoardNotation() {
    }

    public static String columnToLabel(int zeroBasedColumn) {
        if (zeroBasedColumn < 0) {
            throw new IllegalArgumentException("Az oszlopindex nem lehet negatív.");
        }
        long value = (long) zeroBasedColumn + 1L;
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            value--;
            sb.insert(0, (char) ('a' + (value % 26)));
            value /= 26;
        }
        return sb.toString();
    }

    public static int labelToColumn(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Az oszlopjelölés nem lehet üres.");
        }
        String normalized = label.trim().toLowerCase(Locale.ROOT);
        long column = 0;
        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            if (ch < 'a' || ch > 'z') {
                throw new IllegalArgumentException("Érvénytelen oszlopjelölés: " + label);
            }
            column = column * 26 + (ch - 'a' + 1);
            if (column > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Túl nagy oszlopindex: " + label);
            }
        }
        return (int) column - 1;
    }

    public static String formatCell(Coordinate coordinate) {
        if (coordinate == null || coordinate.row() < 0 || coordinate.col() < 0) {
            throw new IllegalArgumentException("Érvénytelen koordináta.");
        }
        return columnToLabel(coordinate.col()) + (coordinate.row() + 1);
    }

    public static Optional<Coordinate> parseCell(String text) {
        if (text == null) {
            return Optional.empty();
        }
        String value = text.trim().toLowerCase(Locale.ROOT);
        int idx = 0;
        while (idx < value.length() && Character.isLetter(value.charAt(idx))) {
            idx++;
        }
        if (idx == 0 || idx == value.length()) {
            return Optional.empty();
        }
        String colPart = value.substring(0, idx);
        String rowPart = value.substring(idx);
        if (!rowPart.chars().allMatch(Character::isDigit)) {
            return Optional.empty();
        }
        try {
            int col = labelToColumn(colPart);
            int row = Integer.parseInt(rowPart);
            if (row <= 0) {
                return Optional.empty();
            }
            return Optional.of(new Coordinate(row - 1, col));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
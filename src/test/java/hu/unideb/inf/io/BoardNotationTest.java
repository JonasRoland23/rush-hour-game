package hu.unideb.inf.io;

import hu.unideb.inf.model.Coordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardNotationTest {

    @Test
    void testColumnToLabelShouldConvertZeroBasedIndexes() {
        assertEquals("a", BoardNotation.columnToLabel(0));
        assertEquals("c", BoardNotation.columnToLabel(2));
        assertEquals("z", BoardNotation.columnToLabel(25));
        assertEquals("aa", BoardNotation.columnToLabel(26));
        assertThrows(IllegalArgumentException.class, () -> BoardNotation.columnToLabel(-1));
    }

    @Test
    void testLabelToColumnShouldConvertLettersToZeroBasedIndexes() {
        assertEquals(0, BoardNotation.labelToColumn("a"));
        assertEquals(2, BoardNotation.labelToColumn("C"));
        assertEquals(25, BoardNotation.labelToColumn("z"));
        assertEquals(26, BoardNotation.labelToColumn("aa"));
        assertThrows(IllegalArgumentException.class, () -> BoardNotation.labelToColumn(""));
        assertThrows(IllegalArgumentException.class, () -> BoardNotation.labelToColumn("12"));
    }

    @Test
    void testFormatCellShouldProduceChessLikeLabel() {
        assertEquals("a1", BoardNotation.formatCell(new Coordinate(0, 0)));
        assertEquals("l6", BoardNotation.formatCell(new Coordinate(5, 11)));
        assertThrows(IllegalArgumentException.class, () -> BoardNotation.formatCell(null));
        assertThrows(IllegalArgumentException.class, () -> BoardNotation.formatCell(new Coordinate(-1, 0)));
    }

    @Test
    void testParseCellShouldParseValidLabelsCaseInsensitively() {
        assertEquals(new Coordinate(0, 0), BoardNotation.parseCell("a1").orElseThrow());
        assertEquals(new Coordinate(2, 3), BoardNotation.parseCell("D3").orElseThrow());
        assertEquals(new Coordinate(1, 26), BoardNotation.parseCell("aa2").orElseThrow());
    }

    @Test
    void testParseCellShouldReturnEmptyForInvalidInputs() {
        assertTrue(BoardNotation.parseCell(null).isEmpty());
        assertTrue(BoardNotation.parseCell("").isEmpty());
        assertTrue(BoardNotation.parseCell("1a").isEmpty());
        assertTrue(BoardNotation.parseCell("a0").isEmpty());
        assertTrue(BoardNotation.parseCell("a").isEmpty());
        assertTrue(BoardNotation.parseCell("á1").isEmpty());
        assertTrue(BoardNotation.parseCell("a99999999999999999999").isEmpty());
    }
}

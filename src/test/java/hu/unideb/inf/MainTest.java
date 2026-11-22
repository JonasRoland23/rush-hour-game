package hu.unideb.inf;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void testMainShouldStartGameAndExitCleanly() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("1\nTesztElek\nexit\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

            Main.main(new String[0]);

            assertTrue(out.toString(StandardCharsets.UTF_8).contains("=== RUSH HOUR JÁTÉK ==="));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}

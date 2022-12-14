package io.github.theprez.jfiletail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class JFileTail {
    private static final int CHUNK_SIZE = 4 * 1024;

    public static void main(final String[] args) throws IOException {
        // System.out.println("hi");
        final File f = new File(args.length < 1 ? "test.txt" : args[0]);

        try (FileNewContentsReader reader = new FileNewContentsReader(f, "*TAG"); PrintWriter stdout = new PrintWriter(System.out);) {
            final char[] buffer = new char[8192];
            int nRead = -1;
            while ((nRead = reader.read(buffer, 0, buffer.length)) >= 0) {
                stdout.write(buffer, 0, nRead);
            }
        }
    }
}
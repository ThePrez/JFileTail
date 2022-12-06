package io.github.theprez.jfiletail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class JFileTail {
    private static final int CHUNK_SIZE = 4 * 1024;

    public static void main(String[] args) throws IOException {
        // System.out.println("hi");
        File f = new File(args.length < 1 ? "test.txt":args[0]);

        try(FileNewContentsReader reader = new FileNewContentsReader(f, "*TAG"); PrintWriter stdout = new PrintWriter(System.out);) {
            reader.transferTo(stdout);
        }
    }
}
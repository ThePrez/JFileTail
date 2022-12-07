package io.github.theprez.jfiletail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileIdGetter {
    // example value :
    // (dev=8000804000000001,ino=3269666)
    private static final Pattern s_inoPattern = Pattern.compile("(st_ino|inode|ino)\\s?=\\s?([0-9]+)");

    public static void main(final String[] args) throws Exception {
        final String filename = args.length == 0 ? "test.txt" : args[0];
        final File file = new File(filename);
        System.out.printf("inode for file '%s' is '%s'\n", file.toString(), new FileIdGetter(file).get());
    }

    private final File m_file;

    public FileIdGetter(final File _f) {
        m_file = _f;
    }

    public long get() throws IOException {
        final String pathStr = m_file.getCanonicalPath();
        final Path path = Paths.get(m_file.getAbsolutePath());
        final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        final Object inode = attr.fileKey();

        if (null == inode) {
            return pathStr.hashCode() & 0xFFFFFFFFL;
        }
        try {
            final Field inoFld = inode.getClass().getDeclaredField("st_ino");
            inoFld.setAccessible(true);
            return inoFld.getLong(inode);
        } catch (final Exception e) {
            final Matcher m = s_inoPattern.matcher(inode.toString());
            try {
                m.find();
                return Long.parseLong(m.group(2));
            } catch (final Exception e2) {
                return pathStr.hashCode() & 0xFFFFFFFFL;
            }

        }
    }
}

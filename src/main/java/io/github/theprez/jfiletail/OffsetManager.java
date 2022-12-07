package io.github.theprez.jfiletail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

abstract class OffsetManager {
    protected static FileTime getCreationTime(final File _f) throws IOException {
        final Path path = Paths.get(_f.getAbsolutePath());
        final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return attr.creationTime();
    }

    protected static FileTime getModifyTime(final File _f) throws IOException {
        final Path path = Paths.get(_f.getAbsolutePath());
        final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return attr.lastModifiedTime();
    }

    protected final File m_file;

    protected OffsetManager(final File _f) {
        m_file = _f;
    }

    abstract public long get() throws IOException;

    public boolean isOlderThan(final FileTime _x) throws IOException {
        final FileTime time = getCreationTime(m_file);
        return (time.compareTo(_x) < 0);
    }

    abstract public void set(long _l) throws IOException;
}
package io.github.theprez.jfiletail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

abstract class OffsetManager {
    abstract public long get() throws IOException;

    abstract public void set(long _l) throws IOException;

    protected final File m_file;

    protected OffsetManager(File _f) {
        m_file = _f;
    }
    
    protected static FileTime getCreationTime(File _f) throws IOException {
        Path path = Paths.get(_f.getAbsolutePath());
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return attr.creationTime();
    }
    protected static FileTime getModifyTime(File _f) throws IOException {
        Path path = Paths.get(_f.getAbsolutePath());
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return attr.lastModifiedTime();
    }

    public boolean isOlderThan(FileTime _x) throws IOException {
        FileTime time = getCreationTime(m_file);
        return (time.compareTo(_x)<0);
    }
}
package io.github.theprez.jfiletail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class OffsetFile extends OffsetManager {
    private final File m_offsetFile;
    private final File m_nameFile;
    

    public OffsetFile(final File _f) throws IOException {
        super(_f);
        File dotDir = new File(new File(System.getProperty("user.home", "~")), ".jtailor");
        dotDir.mkdirs();
        m_offsetFile = new File(dotDir,"."+new FileIdGetter(_f).get()+ ".offset");
     m_nameFile = new File(dotDir,"."+new FileIdGetter(_f).get()+ ".filename");
        try(FileWriter nameWriter = new FileWriter(m_nameFile)) {
            nameWriter.write(_f.getCanonicalPath());
        }
    }

    @Override
    public synchronized long get() throws IOException {
        if (!m_offsetFile.exists()) {
            m_offsetFile.createNewFile();
            return 0;
        } else if (0 == m_offsetFile.length()) {
            return 0;
        }

        if (!super.isOlderThan(getModifyTime(m_offsetFile))) {
            Debug.debug("Warning: file has changed");
            return 0;
        }

        try (FileInputStream fis = new FileInputStream(m_offsetFile)) {
            byte[] valBuf = new byte[8];
            int bytesRead = fis.read(valBuf);
            if (8 != bytesRead) {
                throw new IOException("OFfset file corrupt");
            }
            long ofs = ByteBuffer.wrap(valBuf).getLong();
            if(ofs > m_file.length()) {
                Debug.debug("Warning: file has changed (now smaller)");
                return 0;
            }
            return ofs;
        }
    }

    @Override
    public synchronized void set(long _l) throws IOException {
        byte[] buf = new byte[8];
        ByteBuffer.wrap(buf).putLong(_l);
        try (FileOutputStream fos = new FileOutputStream(m_offsetFile)) {
            fos.write(buf);
        }
    }

    public boolean delete() {
        return m_offsetFile.delete() && m_nameFile.delete();
    }
}

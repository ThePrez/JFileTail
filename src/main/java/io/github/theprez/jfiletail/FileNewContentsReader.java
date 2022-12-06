package io.github.theprez.jfiletail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class FileNewContentsReader extends Reader {

    private long m_newPos;
    private final InputStream m_in;
    private final OffsetFile m_ofs;
    private final long m_end;
    private final long m_start;
    private final String m_encoding;
    private final byte m_newLineChar;

    public static void reset(File _f) throws IOException
    {
        OffsetFile r = new OffsetFile(_f);
        r.delete();
    }


    public FileNewContentsReader(File _f) throws IOException {
        this(_f, "*TAG");
    }
        public FileNewContentsReader(File _f, final String _encoding) throws IOException {
        m_ofs = new OffsetFile(_f);
        m_end = _f.length();
        m_start = m_ofs.get();
        m_newPos = m_start;
        FileInputStream fileIn = new FileInputStream(_f);
        fileIn.skip(m_start);
        m_in = new BufferedInputStream(fileIn, Math.max(1, (int) Math.min(m_end-m_start, 1024*512)));
        m_encoding = "*TAG".equals(_encoding) ? CcsidUtils.getTaggedCcsidAndEncoding(_f).getValue() : _encoding;
        Debug.debug("-->offset=" + m_start);
        Debug.debug("-->filesize=" + m_end);
        CharsetEncoder encoder = Charset.forName(m_encoding).newEncoder();
        ByteBuffer encoded = encoder.encode(CharBuffer.wrap("\n"));
        m_newLineChar = encoded.get(encoded.limit() - 1);
    }

    @Override
    public synchronized int read(char[] _cbuf, int _off, int _len) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            if (m_start == m_end) {
                return -1;
            }
            int i = m_in.read();
            if (-1 == i) {
                Debug.debug("-->EOF");
                break;
            }
            m_newPos++;
            baos.write(i);
            if (baos.size() >= _len) {
                Debug.debug("-->friendly break, max length processed");
                break;
            }
            if (m_newLineChar == i) {
                Debug.debug("-->friendly break, newline");
                break;
            }
            if (m_newPos >= m_end) {
                Debug.debug("-->friendly break, end of read block");
                break;
            }
        }
        if (0 == baos.size()) {
            return -1;
        }
        char[] results = new String(baos.toByteArray(), m_encoding).toCharArray();
        System.arraycopy(results, 0, _cbuf, _off, Math.min(_len, results.length));
        return results.length;
    }

    @Override
    public void close() throws IOException {
        Debug.debug("-->closing");
        m_ofs.set(m_newPos);
        Debug.debug("-->closed");
    }
}

package com.github.tototoshi.csv;

import java.io.IOException;
import java.io.Reader;

/** Splits lines over an internally owned buffer, refilled from the Reader. */
public class ReaderLineReader extends AbstractBufferedLineReader {

    private final Reader reader;

    public ReaderLineReader(Reader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    ReaderLineReader(Reader reader, int bufferSize) {
        super(bufferSize);
        this.reader = reader;
    }

    @Override
    protected int fillBuffer(char[] dst) throws IOException {
        return reader.read(dst, 0, dst.length);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}

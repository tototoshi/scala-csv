package com.github.tototoshi.csv;

import scala.collection.Iterator;
import scala.io.Source;

import java.io.IOException;

/**
 * Splits lines over an internally owned buffer.
 *
 * Source offers no bulk read, so the refill still pulls one char at a time;
 * the win is that it pulls them into a char[] rather than appending each to a
 * StringBuilder, and that it goes through Source.iter() rather than
 * Source.next(). The latter matters more than it looks: Source.next() runs a
 * Positioner per character, encoding a position and tracking line and column,
 * all of which a line reader throws away.
 */
public class SourceLineReader extends AbstractBufferedLineReader {

    private final Source source;
    private final Iterator<Object> iter;

    public SourceLineReader(Source source) {
        this(source, DEFAULT_BUFFER_SIZE);
    }

    SourceLineReader(Source source, int bufferSize) {
        super(bufferSize);
        this.source = source;
        this.iter = source.iter();
    }

    @Override
    protected int fillBuffer(char[] dst) {
        int n = 0;
        while (n < dst.length && iter.hasNext()) {
            dst[n++] = (Character) iter.next();
        }
        return n == 0 ? -1 : n;
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}

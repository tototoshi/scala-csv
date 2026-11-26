package com.github.tototoshi.csv;

import java.io.IOException;

/**
 * Line splitting over a buffer the reader owns: the terminator hunt is a scan
 * of a local char[] rather than a virtual read per character, and the CR
 * lookahead is a peek at the next slot. Subclasses supply only the refill.
 */
abstract class AbstractBufferedLineReader implements LineReader {

    static final int DEFAULT_BUFFER_SIZE = 8192;

    private final char[] buf;
    private int pos;
    private int limit;
    private boolean eof;

    AbstractBufferedLineReader(int bufferSize) {
        if (bufferSize < 2) {
            throw new IllegalArgumentException("buffer size must be >= 2");
        }
        this.buf = new char[bufferSize];
    }

    /** Fills dst from the start, returning the count, or -1 once exhausted. */
    protected abstract int fillBuffer(char[] dst) throws IOException;

    @Override
    public final String readLineWithTerminator() throws IOException {
        // Stays null while the line lies inside one buffer-load, which is the
        // overwhelmingly common case; then the result is built straight from
        // the char[] with no intermediate copy.
        StringBuilder sb = null;

        for (;;) {
            if (pos >= limit && !fill()) {
                return sb == null ? null : sb.toString();
            }

            final char[] b = buf;
            final int lim = limit;
            final int start = pos;
            int i = start;

            while (i < lim) {
                final char c = b[i];

                if (c == '\r') {
                    i++;
                    if (i < lim) {
                        if (b[i] == '\n') {
                            i++;
                        }
                        pos = i;
                        return finish(sb, b, start, i - start);
                    }
                    // CR is the last char in the buffer, so the LF that may
                    // pair with it is on the far side of a refill.
                    sb = collect(sb, b, start, i - start);
                    pos = i;
                    if (fill() && buf[pos] == '\n') {
                        sb.append('\n');
                        pos++;
                    }
                    return sb.toString();
                }

                if (c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085') {
                    i++;
                    pos = i;
                    return finish(sb, b, start, i - start);
                }

                i++;
            }

            sb = collect(sb, b, start, lim - start);
            pos = lim;
        }
    }

    private static String finish(StringBuilder sb, char[] b, int off, int len) {
        return sb == null ? new String(b, off, len) : sb.append(b, off, len).toString();
    }

    private static StringBuilder collect(StringBuilder sb, char[] b, int off, int len) {
        if (sb == null) {
            sb = new StringBuilder(Math.max(len + 16, 64));
        }
        return sb.append(b, off, len);
    }

    /** @return false once the source is exhausted. */
    private boolean fill() throws IOException {
        if (eof) {
            return false;
        }
        int n;
        do {
            n = fillBuffer(buf);
        } while (n == 0);
        if (n < 0) {
            eof = true;
            pos = 0;
            limit = 0;
            return false;
        }
        pos = 0;
        limit = n;
        return true;
    }
}

package com.github.tototoshi.csv;

import scala.io.Source;

import java.io.IOException;

public class SourceLineReader implements LineReader {

    private Source reader;

    public SourceLineReader(Source reader) {
        this.reader = reader;
    }

    @Override
    public String readLineWithTerminator() throws IOException {
        StringBuilder sb = new StringBuilder();
        while(true) {
            if (!reader.hasNext()) {
                if (sb.length() == 0) {
                    return null;
                } else {
                    break;
                }
            }
            int c = reader.next();

            sb.append((char) c);

            if (c == '\n'
                    || c == '\u2028'
                    || c == '\u2029'
                    || c == '\u0085') {
                break;
            }

            if (c == '\r') {
                if (!reader.hasNext()) {
                    break;
                }
                c = reader.next();
                sb.append(c);
                if (c == '\n') {
                    break;
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}

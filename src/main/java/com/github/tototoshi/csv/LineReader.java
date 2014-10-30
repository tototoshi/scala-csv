package com.github.tototoshi.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LineReader {

    private BufferedReader bufferedReader;

    public LineReader(Reader reader) {
        this.bufferedReader = new BufferedReader(reader);
    }

    public String readLineWithTerminator() throws IOException {
        int c = -1;
        StringBuilder sb = new StringBuilder();
        do {

            c = bufferedReader.read();

            if (c == -1) {
                if (sb.length() == 0) {
                    return null;
                } else {
                    break;
                }
            }

            sb.append((char) c);

            if (c == '\n') {
                break;
            }

            if (c == '\r') {

                bufferedReader.mark(1);

                c = bufferedReader.read();

                if (c == -1) {
                    break;
                } else if (c == '\n') {
                    sb.append('\n');
                } else {
                    bufferedReader.reset();
                }

                break;
            }

        } while (true);

        return sb.toString();
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}
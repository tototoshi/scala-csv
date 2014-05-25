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
        int c = bufferedReader.read();

        if (c == -1) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append((char) c);

        while (true) {

            c = bufferedReader.read();

            if (c == -1) {
                break;
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

        }

        return sb.toString();
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}

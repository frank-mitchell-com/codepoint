/*
 * Copyright 2019 Frank Mitchell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.frank_mitchell.codepoint.spi;

import com.frank_mitchell.codepoint.CodePointSource;
import com.frank_mitchell.codepoint.ForCharsets;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UTFDataFormatException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@link CodePointSource} that wraps a {@link Reader}.
 * 
 * @author fmitchell
 */
public final class ReaderSource implements CodePointSource {

    private final Reader _reader;
    private int _lastChar;
    private int _nextChar;

    @ForCharsets(names={"UTF-16","UTF-16BE","UTF-16LE"})
    public ReaderSource(Reader r) throws IOException {
        this(r, StandardCharsets.UTF_16);
    }

    public ReaderSource(Reader r, Charset cs) throws IOException {
        // TODO: Not using the charset
        _reader = r;
        _lastChar = -1;
        _nextChar = -1;
    }

    public ReaderSource(InputStream s) throws IOException {
        this(s, StandardCharsets.UTF_8);
    }

    public ReaderSource(InputStream s, Charset e) throws IOException {
        this(new InputStreamReader(s, e));
    }

    private Object getLock() {
        return this;
    }

    @Override
    public int getCodePoint() {
        synchronized (getLock()) {
            if (_lastChar < 0) {
                throw new IllegalStateException("have not called next() yet");
            }
            return _lastChar;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        synchronized (getLock()) {
            if (_nextChar < 0) {
                _nextChar = _reader.read();
            }
            return _nextChar > 0;
        }
    }

    @Override
    public void next() throws IOException {
        synchronized (getLock()) {
            int cp;
            if (_nextChar > 0) {
                cp = _nextChar;
            } else {
                cp = _reader.read();
            }
            if (cp >= 0 && Character.isSurrogate((char)cp)) {
                cp = toCodePoint(cp, _reader.read());
            }
            _lastChar = cp;
            _nextChar = -1;
        }
    }

    private int toCodePoint(int c1, int c2) throws UTFDataFormatException, EOFException {
        int cp;
        if (c2 < 0) {
            throw new EOFException("Incomplete surrogate pair: "
                    + Integer.toHexString(c2));
        }
        if (Character.isHighSurrogate((char)c1)
                && Character.isLowSurrogate((char)c2)) {
            cp = Character.toCodePoint((char)c1, (char)c2);
        } else if (Character.isHighSurrogate((char)c2)
                && Character.isLowSurrogate((char)c1)) {
            cp = Character.toCodePoint((char)c2, (char)c1);
        } else {
            throw new UTFDataFormatException("Mismatched surrogate pair: "
                    + Integer.toHexString(c1) + " "
                    + Integer.toHexString(c2));
        }
        return cp;
    }

    @Override
    public void close() throws IOException {
        _reader.close();
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.frank_mitchell.codepoint.CodePointSource;

public final class ReaderSource implements CodePointSource {

    private final Reader _reader;
    private int _lastChar;
    private int _nextChar;

    public ReaderSource(Reader r) throws IOException {
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
        /*
         * TODO: The user shouldn't be able to call this method
         * before calling next(), and yet they do. That's why
         * _lastChar is set to ' '. Fixing this will require
         * rewriting DefaultJsonLexer, though. 
         */
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
            if (_nextChar > 0) {
                _lastChar = _nextChar;
            } else {
                _nextChar = _reader.read();
            }
            _nextChar = -1;
            /*
             * TODO: If _lastChar is part of a multi-byte UTF-16
             * code point, read the next char and decode them.
             * We are reading code points, after all.
             */
        }
    }

    @Override
    public void close() throws IOException {
        _reader.close();
    }
}

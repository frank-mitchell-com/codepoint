/*
 * The MIT License
 *
 * Copyright 2023 fmitchell.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.frank_mitchell.codepoint.spi;

import com.frank_mitchell.codepoint.CodePointSink;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@link CodePointSink} that wraps a {@link StringBuffer}.
 *
 * @author Frank Mitchell
 */
class StringBufferSink implements CodePointSink {

    private final StringBuffer _buffer;
    private final boolean _littleEndian;

     /**
     * Wrap this object around a writer.
     *
     * @param b the buffer
     * @param cs the charset this object is writing
     */
    public StringBufferSink(final StringBuffer b, final Charset cs) {
        _buffer = b;
        _littleEndian = (cs == StandardCharsets.UTF_16LE);
    }

    private Object getLock() {
        return this;
    }

    @Override
    public void putCodePoint(int cp) throws IOException {
        synchronized (getLock()) {
            if (cp <= 0xFFFF) {
                _buffer.append((char)cp);
            } else if (_littleEndian) {
                _buffer.append(Character.lowSurrogate(cp));
                _buffer.append(Character.highSurrogate(cp));
            } else {
                _buffer.append(Character.highSurrogate(cp));
                _buffer.append(Character.lowSurrogate(cp));
            }
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}

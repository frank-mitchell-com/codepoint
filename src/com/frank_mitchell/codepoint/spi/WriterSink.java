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
import com.frank_mitchell.codepoint.ForCharsets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@link CodePointSink} that wraps a {@link Writer}.
 *
 * @author Frank Mitchell
 */
public class WriterSink implements CodePointSink {

    private final Writer _writer;
    private final boolean _littleEndian;

    /**
     * Wrap this object around a writer.
     *
     * @param writer the writer
     */
    @ForCharsets(names={"UTF-16","UTF-16BE"})
    public WriterSink(Writer writer) {
        this(writer, StandardCharsets.UTF_16BE);
    }

     /**
     * Wrap this object around a writer.
     *
     * @param writer the writer
     * @param cs the charset this object is writing
     */
    public WriterSink(final Writer writer, final Charset cs) {
        _writer = writer;
        _littleEndian = (cs == StandardCharsets.UTF_16LE);
    }

    /**
     * Wrap this object around a UTF-8 output stream.
     *
     * @param os the output stream
     */
    @ForCharsets(names={"UTF-8"})
    public WriterSink(OutputStream os) {
        this(os, StandardCharsets.UTF_8);
    }

    /**
     * Wrap this object around an arbitrarily encoded output stream.
     *
     * @param os the output stream
     * @param cs the character set for outgoing bytes
     */
    public WriterSink(OutputStream os, Charset cs) {
        this(new OutputStreamWriter(os, cs), cs);
    }

    private Object getLock() {
        return this;
    }

    @Override
    public void putCodePoint(int cp) throws IOException {
        synchronized (getLock()) {
            if (cp <= 0xFFFF) {
                _writer.write(cp);
            } else if (_littleEndian) {
                _writer.write(Character.lowSurrogate(cp));
                _writer.write(Character.highSurrogate(cp));
            } else {
                _writer.write(Character.highSurrogate(cp));
                _writer.write(Character.lowSurrogate(cp));
            }
        }
    }

    @Override
    public void flush() throws IOException {
        _writer.flush();
    }

    @Override
    public void close() throws IOException {
        _writer.close();
    }
}

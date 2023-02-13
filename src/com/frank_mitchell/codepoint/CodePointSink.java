/*
 * Copyright 2023 Frank Mitchell
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
package com.frank_mitchell.codepoint;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

/**
 * Write Unicode code points to external output.
 *
 * @author Frank Mitchell
 */
public interface CodePointSink extends Appendable, Flushable, Closeable {
    /**
     * Writes a single code point to underlying output.
     * @param cp code point
     * @throws IOException if the underlying output throws an exception
     */
    void putCodePoint(int cp) throws IOException;

    /**
     * Write a stream of code points to underlying output.
     * @param cps stream of code points
     * @throws IOException if the underlying output throws an exception
     */
    default void putCodePoints(final IntStream cps) throws IOException {
        PrimitiveIterator.OfInt iter = cps.iterator();
        while (iter.hasNext()){
            putCodePoint(iter.nextInt());
        }
    }

    @Override
    default Appendable append(final char c) throws IOException {
        // TODO: keep a buffer so we can detect surrogates?
        putCodePoint(c);
        return this;
    }

    @Override
    default Appendable append(final CharSequence csq) throws IOException {
        putCodePoints(csq.codePoints());
        return this;
    }

    @Override
    default Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
        return append(csq.subSequence(start, end));
    }

    /**
     * Flush the underlying output's buffers, if any.
     * @throws IOException if the underlying output throws an exception
     */
    @Override
    void flush() throws IOException;

    /**
     * Close the underlying output.
     * @throws IOException if the underlying output throws an exception
     */
    @Override
    void close() throws IOException;
}

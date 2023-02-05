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

package com.frank_mitchell.codepoint;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * An iterator over an external sequence of Unicode code points.
 * Using <code>int</code> instead of <code>char</code> is a bit of 
 * future-proofing for when streams commonly contain characters
 * outside of the Basic Multilingual Plane (0x0000 - 0xFFFF).
 * Implementers can transparently decode UTF-8 or UTF-16 multi-byte
 * characters into a single code point.  (At least until Unicode expands
 * past 32 bits.)
 * 
 * Unlike standard Java {@link Iterator}s, advancing the iterator and
 * reading the next item in the sequence can be two separate actions.
 * That way one can pass the source to other methods and they can read
 * the last code point read without altering state.
 * 
 * @author Frank Mitchell
 */
public interface CodePointSource extends Closeable {

    /**
     * Read the current code point after the last call to {@link #next()}.
     * 
     * @return current code point.
     */
    int getCodePoint();

    /**
     * Whether this source still has code points remaining.
     * This method may read ahead to the next character, which may
     * cause an exception.
     * 
     * @return whether this object has a next code point.
     * 
     * @throws java.io.IOException if read-ahead throws an exception
     */
    boolean hasNext() throws IOException;

    /**
     * Get the next code point.
     * 
     * @throws IOException 
     */
    void next() throws IOException;
   
    /**
     * Close the underlying IO or NIO object.
     * 
     * @throws IOException from the underlying object.
     */
    @Override
    void close() throws IOException;
 }
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

/**
 * Write Unicode code points to external output.
 * 
 * @author Frank Mitchell
 */
public interface CodePointSink extends Appendable, Flushable, Closeable {
    void putCodePoint(int cp) throws IOException;
    
    default void putCodePoints(int[] cpbuf, int offset, int len) throws IOException {
        for (int i = offset; i < len; i++) {
            putCodePoint(cpbuf[i]);
        }
    }

    void putCharSequence(CharSequence seq) throws IOException;

    void putChars(char[] cbuf, int offset, int len) throws IOException;

    void flush() throws IOException;
    
    void close() throws IOException;
}

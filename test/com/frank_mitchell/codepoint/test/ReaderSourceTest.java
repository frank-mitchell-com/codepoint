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
package com.frank_mitchell.codepoint.test;

import com.frank_mitchell.codepoint.CodePointSource;
import com.frank_mitchell.codepoint.spi.ReaderSource;
import java.io.*;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author fmitchell
 */
public class ReaderSourceTest extends CodePointSourceTest {

    @Override
    public Object createBackingStore() {
        return new StringBuilder();
    }

    @Override
    public CodePointSource createCodePointSource(Object store) throws IOException {
        // Can't use a mock object because Reader is a class, so ...
        return new ReaderSource(new FakeReader((CharSequence) store));
    }

    @Override
    public void push(String text) {
        ((StringBuilder) _store).append(text);
    }

    public static class FakeReader extends Reader {
        /*
         * The parser likes to read one or two characters ahead, so we'll give
         * it some whitespace until we have real contents.
         */
        final CharSequence _input;
        boolean _closed = false;
        int _pos = 0;

        public FakeReader(CharSequence cs) {
            _input = cs;
        }

        @Override
        public int read(char[] chars, int off, int len) throws IOException {

            assertTrue("not closed", !_closed);
            if (_pos >= _input.length()) {
                return -1;
            } else {
                /*
                 * I *could* figure out the maximum chars I can safely read,
                 * but the implementation only reads one at a time anyway,
                 * so ...
                 */
                chars[off] = _input.charAt(_pos);
                _pos += 1;
                return 1;
            }
        }

        @Override
        public void close() throws IOException {
            _closed = true;
        }
    }
    
    // TODO: Throw an exception from inside the Reader.

}
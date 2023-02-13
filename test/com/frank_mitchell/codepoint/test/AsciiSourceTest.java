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
import com.frank_mitchell.codepoint.spi.AsciiSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.Assert;

/**
 *
 * @author fmitchell
 */
public final class AsciiSourceTest extends CodePointSourceTest {

    @Override
    public Object createBackingStore() {
        return new FakeInputStream();
    }

    @Override
    public CodePointSource createCodePointSource(Object store) {
        return new AsciiSource((InputStream) store);
    }

    @Override
    public void push(String text) {
        ((FakeInputStream)_store).append(text);
    }
 

    public static class FakeInputStream extends InputStream {
        /*
         * The parser likes to read one or two characters ahead, so we'll give
         * it some whitespace until we have real contents.
         */
        private boolean _closed = false;
        private byte[] _buffer = new byte[8];
        private int _length = 0;
        private int _pos = 0;

        @Override
        public void close() throws IOException {
            _closed = true;
        }

        @Override
        public int read() throws IOException {
            Assert.assertFalse("closed", _closed);
            Assert.assertTrue("beyond end of byte array", _pos < _buffer.length);
            if (_closed || _pos >= _length) {
                return -1;
            } else {
                Assert.assertTrue("beyond last byte written", _pos < _length);
                int result = _buffer[_pos];
                _pos++;        // I like to increment separately; sue me.
                return result;
            }
        }
        
        protected void append(String text) {
            byte[] newbytes = text.getBytes(StandardCharsets.UTF_8);
            final int newlength = _length + newbytes.length;
            if (newlength > _buffer.length) {
                _buffer = Arrays.copyOf(_buffer, newlength * 2);
            }
            System.arraycopy(newbytes, 0, _buffer, _length, newbytes.length);
            _length = newlength;
        }
    }
}
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * Writes code points to a Byte Buffer.
 * 
 * @author fmitchell
 */
public class ByteBufferSink extends AbstractSink {
    private final ByteBuffer _buf;
    private final CharBuffer _cbuf;
    private final CharsetEncoder _enc;
    
    public ByteBufferSink(ByteBuffer b, Charset cs) {
        _buf = b;
        _cbuf = CharBuffer.allocate(b.capacity());
        _enc = cs.newEncoder();
    }

    @Override
    public void putCodePoint(int cp) throws IOException {
        boolean endofinput = false;
        if (Character.isBmpCodePoint(cp)) {
            _cbuf.put((char)cp);            
        } else {
            int[] iarray = { cp };
            String str = new String(iarray, 0, iarray.length);
            _cbuf.put(str);
        }
        writeToByteBuf(endofinput);
    }

    private void writeToByteBuf(boolean endofinput) throws CharacterCodingException {
        CoderResult result = _enc.encode(_cbuf, _buf, endofinput);
        if (result.isError()) {
            result.throwException();
        }
    }

    @Override
    public void flush() throws IOException {
        writeToByteBuf(true);
    }

    @Override
    public void close() throws IOException {
        flush();
    }
    
}

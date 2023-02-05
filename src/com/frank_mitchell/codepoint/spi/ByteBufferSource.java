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

import com.frank_mitchell.codepoint.CodePointSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * Wraps an IntStream with Unicode code points.
 * 
 * @author Frank Mitchell
 */
public class ByteBufferSource implements CodePointSource {
    private final ByteBuffer _buf;
    private final CharsetDecoder _dec;
    private final CharBuffer _cbuf;
    private boolean _atstart;
    private int _codepoint;
    
    /**
     * Create an instance for UTF-8 bytes
     * 
     * @param bb array of UTF-8 bytes
     */
    public ByteBufferSource(byte[] bb) {
        this(ByteBuffer.wrap(bb), StandardCharsets.UTF_8);
    }
    
    /**
     * Create an instance for UTF-8 bytes
     * 
     * @param bb      array of UTF-8 bytes
     * @param offset  offset at which to start
     * @param length  length at which to end
     */
    public ByteBufferSource(byte[] bb, int offset, int length) {
        this(ByteBuffer.wrap(bb, offset, length), StandardCharsets.UTF_8);
    }
    
    /**
     * Create an instance for UTF-8 bytes
     * 
     * @param b  bytes to decode
     */
    public ByteBufferSource(ByteBuffer b) {
        this(b, StandardCharsets.UTF_8);
    }
    
    /**
     * Create an instance for an arbitrary charset
     * 
     * @param bb array of encoded bytes
     * @param cs encoding of the bytes
     */
    public ByteBufferSource(byte[] bb, Charset cs) {
        this(ByteBuffer.wrap(bb), cs);
    }
    
    /**
     * Create an instance for an arbitrary charset
     * 
     * @param bb      array of encoded bytes
     * @param offset  offset at which to start
     * @param length  length at which to end
     * @param cs      encoding of the bytes
     */
    public ByteBufferSource(byte[] bb, int offset, int length, Charset cs) {
        this(ByteBuffer.wrap(bb, offset, length), cs);
    }
    
    /**
     * Create an instance for an arbitrary Charset.
     * 
     * @param b  bytes to decode
     * @param cs expected character set
     */
    public ByteBufferSource(ByteBuffer b, Charset cs) {
        _buf = b;
        _dec = cs.newDecoder();
        _dec.onMalformedInput(CodingErrorAction.REPORT);
        _dec.onUnmappableCharacter(CodingErrorAction.REPORT);
        _cbuf = CharBuffer.allocate(b.capacity());
        _codepoint = -1;
        _atstart = true;
    }
    
    @Override
    public int getCodePoint() {
        synchronized (this) {
            return _codepoint;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        return _atstart || _cbuf.hasRemaining();
    }

    @Override
    public void next() throws IOException {
        synchronized (this) {
            final boolean endofinput = !_atstart && _buf.hasRemaining();
            CoderResult result = _dec.decode(_buf, _cbuf, endofinput);
            _atstart = false;
            if (result.isError()) {
                result.throwException();
            }
            final int pos = _cbuf.position();
            final char c = _cbuf.get();
            if (Character.isHighSurrogate(c) && hasNext()) {
                final int cp = Character.codePointAt(_cbuf, pos);
                for (int i = 1; i < Character.charCount(cp); i++) {
                    // clear the characters we just read
                    _cbuf.get();
                }
                _codepoint = cp;
            } else {
                _codepoint = c;
            }
        }
    }

    @Override
    public void close() {
    }
}

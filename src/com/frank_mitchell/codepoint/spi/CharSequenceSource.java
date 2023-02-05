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

/**
 * Wraps an arbitrary CharSequence, from a {@link String} to a 
 * {@link CharBuffer}.
 * 
 * @author Frank Mitchell
 */
public class CharSequenceSource implements CodePointSource {
    private final CharSequence _seq;
    private int _pos;
    
    public CharSequenceSource(CharSequence s) {
        _seq = s;
        _pos = -1;
    }
    
    @Override
    public int getCodePoint() {
        synchronized (this) {
            if (_pos < 0 || _pos >= _seq.length()) {
               throw new IllegalStateException();
            }
            final char c = _seq.charAt(_pos);
            if (Character.isHighSurrogate(c) && hasNext()) {
                final int cp = Character.codePointAt(_seq, _pos);
                _pos += Character.charCount(cp);
                return cp;
            } else {
                return c;
            }
        }
    }

    @Override
    public boolean hasNext() {
        synchronized (this) {
            return (_pos + 1 < _seq.length());
        }
    }

    @Override
    public void next() throws IOException {
        synchronized (this) {
            if (hasNext()) {
              _pos++;
            } else {
                throw new java.io.EOFException("End of character sequence");
            }
        }
    }

    @Override
    public void close() {
    }
}

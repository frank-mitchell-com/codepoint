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

/**
 * 
 * @author Frank Mitchell
 */
final class CharWrapper implements CharSequence {
    private final int    len;
    private final char[] cbuf;
    private final int    offset;

    CharWrapper(int len, char[] cbuf, int offset) {
        this.len = len;
        this.cbuf = cbuf;
        this.offset = offset;
    }

    @Override
    public char charAt(int index) {
        return cbuf[offset + index];
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int newlen = len + start - end;
        return new CharWrapper(newlen, cbuf, offset + start);
    }
}
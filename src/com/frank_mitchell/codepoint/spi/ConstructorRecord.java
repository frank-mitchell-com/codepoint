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

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.Set;

/**
 *
 * @author fmitchell
 */
class ConstructorRecord<S> {
    
    Constructor<? extends S> _cons;
    Set<Charset> _charsets;

    ConstructorRecord(Constructor<? extends S> c, Set<Charset> css) {
        _cons = c;
        _charsets = css;
    }

    Constructor<? extends S> getConstructor() {
        return _cons;
    }

    Set<Charset> getCharsets() {
        return _charsets;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("('").append(_cons).append("' ").append(_charsets).append(")");
        return b.toString();
    }
    
}

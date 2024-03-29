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
package com.frank_mitchell.codepoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;

/**
 * An annotation on a {@link CodePointSource} or {@link CodePointSink} that
 * informs {@link CodePoint} that its instances only work on
 * specific character set.
 * For example, a Source might be optimized to read only UTF-8 (and, by
 * extension, ASCII), so it would set {@code ForCharsets("UTF-8")}.
 * Listing "ASCII" would be helpful but not necessary, as {@code CodePoint}
 * infers ASCII from UTF-8.
 *
 * @author Frank Mitchell
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForCharsets {

    /**
     * Denotes the name of a {@link Charset}s this object handles.
     * A name should pass {@link Charset#checkName(java.lang.String)}.
     *
     * @return the name of {@link Charset}s a Source or Sink handles
     */
    String[] names();
}

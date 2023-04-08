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

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Determines a {@link CodePointSource} or {@link CodePointSink} for a given
 * object.
 *
 * It uses the {@link ClassLoader}, configuration files, reflection, and a few
 * heuristics to instantiate an appropriate class. Other implementers of
 * {@link CodePointSource} and {@link CodePointSink} merely need to include in
 * the jar a UTF-8 file named {@code META-INF/codepoint/classes.conf} containing
 * the fully qualified binary names of classes implementing
 * {@link CodePointSource} or {@link CodePointSink}, separated only by
 * whitespace or comments ('#' until the end of the line).
 *
 * Sources and sinks that can only handle a restricted range of {@link Charset}s
 * must indicate which sets with {@link ForCharsets} on the relevant
 * constructor(s).
 *
 * @author Frank Mitchell
 */
public interface CodePointProvider {

    /**
     * Name of the resource from which to load new sources and sinks.
     */
    public static final String CONFIG_FILE = "/codepoint.conf";

    /**
     * Wrap an output object with a {@link CodePointSink}.
     * @param <T> The type of out
     * @param clz the type of out when looking for a suitable wrapper.
     * @param out an object accepting a stream of characters
     * @param cs the {@link Charset} of characters from {@code out}
     * @return a CodePointSink wrapping {@code out}
     * @throws IOException if wrapping or writing to {@code out} caused an exception
     */
    <T> CodePointSink getSink(Class<T> clz, T out, Charset cs) throws IOException;

    /**
     * Wrap an input object with a {@link CodePointSource}.
     * @param <T> The type of {@code in}
     * @param clz the type of {@code in} when looking for a suitable wrapper.
     * @param in an object providing a stream of characters
     * @param cs the {@link Charset} of characters from {@code in}
     * @return a CodePointSource wrapping {@code in}
     * @throws IOException if wrapping or reading from {@code in} caused an exception
     */
    <T> CodePointSource getSource(Class<T> clz, T in, Charset cs) throws IOException;
}

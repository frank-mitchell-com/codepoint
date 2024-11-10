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

import com.frank_mitchell.codepoint.spi.Provider;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Wraps an input or output object with an instance of {@link CodePointSource} 
 * or {@link CodePointSink}.
 * This class and its static methods are a facade for an instance of
 * {@link CodePointProvider}.
 *
 * @author Frank Mitchell
 *
 * @see CodePointProvider
 */
public class CodePoint {

    /*
     * Should load as service, but ...
     */
    private static final CodePointProvider PROVIDER = new Provider();

    private CodePoint() {
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClassFor(T obj) {
        // Not sure how to tell the compiler that the class of T is Class<T>.
        return (Class<T>)obj.getClass();
    }

    /**
     * Wrap an input object with a {@link CodePointSource}.
     * @param <T> The type of in
     * @param in an object providing a stream of characters
     * @param cs the {@link Charset} of characters from in
     * @return a CodePointSource wrapping {@code in}
     * @throws IOException if wrapping or reading in caused an exception
     */
    public static <T> CodePointSource getSource(T in, Charset cs) throws IOException {
        Objects.requireNonNull(in, "No CodePointSource for null");
        assert(in != null);
        return PROVIDER.getSource(getClassFor(in), in, cs);
    }

    /**
     * Wrap an input object with a {@link CodePointSource}.
     * @param <T> The type of in
     * @param clz the type of in when looking for a suitable wrapper.
     * @param in an object providing a stream of characters
     * @param cs the {@link Charset} of characters from in
     * @return a CodePointSource wrapping {@code in}
     * @throws IOException if wrapping or reading in caused an exception
     */
    public static <T> CodePointSource getSource(Class<T> clz, T in, Charset cs) throws IOException {
        return PROVIDER.getSource(clz, in, cs);
    }

    /**
     * Wrap an output object with a {@link CodePointSink}.
     * @param <T> The type of out
     * @param out an object accepting a stream of characters
     * @param cs the {@link Charset} of characters from out
     * @return a CodePointSink wrapping {@code out}
     * @throws IOException if wrapping or writing to out caused an exception
     */
    public static <T> CodePointSink getSink(T out, Charset cs) throws IOException {
        Objects.requireNonNull(out, "No CodePointSink for null");
        assert(out != null);
        return PROVIDER.getSink(getClassFor(out), out, cs);
    }

    /**
     * Wrap an output object with a {@link CodePointSink}.
     * @param <T> The type of out
     * @param clz the type of out when looking for a suitable wrapper.
     * @param out an object accepting a stream of characters
     * @param cs the {@link Charset} of characters from out
     * @return a CodePointSink wrapping {@code out}
     * @throws IOException if wrapping or writing to out caused an exception
     */
    public static <T> CodePointSink getSink(Class<T> clz, T out, Charset cs) throws IOException {
        return PROVIDER.getSink(clz, out, cs);
    }
}

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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Objects;

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
 * must indicate which sets with {@link ForCharset} on the relevant
 * constructor(s).
 *
 * @author Frank Mitchell
 */
public class CodePoint {
    
    private static final CodePointProvider PROVIDER = new Provider();

    /**
     *
     * @param <T>
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    public static <T> CodePointSource getSource(T obj, Charset cs) throws IOException {
        Objects.requireNonNull(obj, "No CodePointSource for null");
        assert(obj != null);
        if (obj instanceof Reader) {
            return PROVIDER.getSource((Reader)obj, cs);
        }
        if (obj instanceof InputStream) {
            return PROVIDER.getSource((InputStream)obj, cs);
        }
        if (obj instanceof CharSequence) {
            return PROVIDER.getSource((CharSequence)obj, cs);
        }
        return PROVIDER.getSource((Class<T>) obj.getClass(), obj, cs);
    }

    /**
     *
     * @param <T>
     * @param clz the value of clz
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    public static <T> CodePointSource getSource(Class<T> clz, T obj, Charset cs) throws IOException {
        return PROVIDER.getSource(clz, obj, cs);
    }

    /**
     *
     * @param <T>
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    public static <T> CodePointSink getSink(T obj, Charset cs) throws IOException {
        Objects.requireNonNull(obj, "No CodePointSink for null");
        assert(obj != null);
        if (obj instanceof Writer) {
            return PROVIDER.getSink((Writer)obj, cs);
        }
        if (obj instanceof OutputStream) {
            return PROVIDER.getSink((OutputStream)obj, cs);
        }
        return PROVIDER.getSink((Class<T>) obj.getClass(), obj, cs);
    }

    /**
     *
     * @param <T>
     * @param clz the value of clz
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    public static <T> CodePointSink getSink(Class<T> clz, T obj, Charset cs) throws IOException {
        return PROVIDER.getSink(clz, obj, cs);
    }
}

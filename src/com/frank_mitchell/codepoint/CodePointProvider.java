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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 *
 * @author fmitchell
 */
public interface CodePointProvider {

    public static final String CONFIG_FILE = "/META-INF/codepoint.conf";

    /**
     *
     * @param <T>
     * @param clz the value of clz
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    <T> CodePointSink getSink(Class<T> clz, T obj, Charset cs) throws IOException;

    /**
     *
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    CodePointSink getSink(OutputStream obj, Charset cs) throws IOException;

    /**
     *
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    CodePointSink getSink(Writer obj, Charset cs) throws IOException;

    /**
     *
     * @param <T>
     * @param clz the value of clz
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    <T> CodePointSource getSource(Class<T> clz, T obj, Charset cs) throws IOException;

    /**
     *
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    CodePointSource getSource(InputStream obj, Charset cs) throws IOException;

    /**
     *
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    CodePointSource getSource(Reader obj, Charset cs) throws IOException;

    /**
     *
     * @param obj the value of obj
     * @param cs the value of cs
     * @return 
     * @throws IOException
     */
    CodePointSource getSource(CharSequence obj, Charset cs) throws IOException;
}

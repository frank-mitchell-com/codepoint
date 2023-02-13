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
package com.frank_mitchell.codepoint.test;

import com.frank_mitchell.codepoint.CodePointSink;
import com.frank_mitchell.codepoint.spi.StringBufferSink;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author fmitchell
 *
 */
public class CodePointSinkTest {

    private CodePointSink _sink;
    protected Object _store;
    
    protected CodePointSink createSink(Object store) {
        return new StringBufferSink((StringBuffer) store, StandardCharsets.UTF_16);
    }

    protected Object createBackingStore() {
        return new StringBuffer();
    }
    
    protected String getOutput() {
        return _store.toString();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _store = createBackingStore();
        _sink = createSink(_store);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        _store = null;
        _sink = null;
    }

    @Test
    public void testPutCodePoint() throws IOException {
        String seq = "\u3041";

        _sink.putCodePoint(seq.codePointAt(0));

        assertEquals(seq, getOutput());
    }
    
    // TODO: Put multiple code points
    // TODO: Test something off the BMP, i.e. > 0x10000
}

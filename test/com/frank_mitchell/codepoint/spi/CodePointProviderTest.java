/*
 * The MIT License
 *
 * Copyright 2023 Frank Mitchell.
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

import com.frank_mitchell.codepoint.CodePointProvider;
import com.frank_mitchell.codepoint.CodePointSink;
import com.frank_mitchell.codepoint.CodePointSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author frank
 */
public class CodePointProviderTest {
    static final String TEXT = "{}";

    CodePointProvider _provider;

    public CodePointProviderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _provider = new Provider();
    }

    @After
    public void tearDown() {
        _provider = null;
    }

    @Test
    public void testGetAsciiSource() throws IOException {
        final Charset cs = StandardCharsets.US_ASCII;
        ByteArrayInputStream input = new ByteArrayInputStream(TEXT.getBytes(cs));
        CodePointSource result = _provider.getSource(getClassFor(input), input, cs);
        assertNotNull(result);
        assertTrue(result.getClass().toString(), result instanceof AsciiSource);

        assertSourceText(TEXT, result);
    }

    @Test
    public void testGetInputStreamSource() throws IOException {
        final Charset cs = StandardCharsets.UTF_8;
        ByteArrayInputStream input = new ByteArrayInputStream(TEXT.getBytes(cs));
        CodePointSource result = _provider.getSource(getClassFor(input), input, cs);
        assertNotNull(result);

        assertSourceText(TEXT, result);
    }

    @Test
    public void testGetReaderSource() throws IOException {
        final Charset cs = StandardCharsets.UTF_16;
        StringReader input = new StringReader(TEXT);
        CodePointSource result = _provider.getSource(getClassFor(input), input, cs);
        assertNotNull(result);

        assertSourceText(TEXT, result);
    }

    @Test
    public void testGetCharSequenceSource() throws IOException {
        final Charset cs = StandardCharsets.UTF_16;
        CodePointSource result = _provider.getSource(getClassFor(TEXT), TEXT, cs);
        assertNotNull(result);

        assertSourceText(TEXT, result);
    }

    static <T> Class<T> getClassFor(T obj) {
        assertNotNull(obj);
        return (Class<T>)obj.getClass();
    }

    void assertSourceText(String expected, CodePointSource actual) throws IOException {
        for (int c : expected.codePoints().toArray()) {
            assertTrue("Has code point", actual.hasNext());
            actual.next();
            assertEquals("Code point", c, actual.getCodePoint());
        }
        assertFalse("No more code points", actual.hasNext());
    }

    @Test
    public void testGetOutputStreamSink() throws IOException {
        final Charset cs = StandardCharsets.UTF_8;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (CodePointSink result = _provider.getSink(getClassFor(output), output, cs)) {
            assertNotNull(result);

            result.append(TEXT);
            result.flush();
        }
        assertEquals(TEXT, output.toString());
    }

    @Test
    public void testGetWriterSink() throws IOException {
        final Charset cs = StandardCharsets.UTF_16;
        StringWriter output = new StringWriter();
        try (CodePointSink result = _provider.getSink(getClassFor(output), output, cs)) {
            assertNotNull(result);

            result.append(TEXT);
            result.flush();
        }
        assertEquals(TEXT, output.toString());
    }

    @Test
    public void testGetStringBufferSink() throws IOException {
        final Charset cs = StandardCharsets.UTF_16;
        StringBuffer output = new StringBuffer();
        try (CodePointSink result = _provider.getSink(getClassFor(output), output, cs)) {
            assertNotNull(result);

            result.append(TEXT);
            result.flush();
        }
        assertEquals(TEXT, output.toString());
    }
}

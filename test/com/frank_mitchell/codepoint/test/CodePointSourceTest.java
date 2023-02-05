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

import java.io.*;
import com.frank_mitchell.codepoint.CodePointSource;
import com.frank_mitchell.codepoint.spi.CharSequenceSource;
import java.util.PrimitiveIterator;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
/**
 *
 * @author fmitchell
 */
public class CodePointSourceTest  {
    protected CodePointSource _source;
    protected Object _store;
    
    /**
     * OVERRIDE this to create another backing store for the object under test.
     * 
     * @return a backing store the class under test can read from
     */
    public Object createBackingStore() {
        return new StringBuilder();
    }
    
    /**
     * OVERRIDE this to test another implementation of {@link CodePointSource}.
     * 
     * @param store a backing store of characters used in the test.
     * @return a source that reads from the backing store
     * @throws java.io.IOException if there's a fictional IOException
     */
    protected CodePointSource createCodePointSource(Object store) throws IOException {
        return new CharSequenceSource((CharSequence)store);
    }

    /**
     * OVERRIDE this to add test text to the object under test.
     * 
     * @param text 
     * @throws java.io.IOException if there's a fictional IOException
     */
    protected void push(String text) throws IOException {
        ((StringBuilder)_store).append(text);
    }
    
    @Before
    public void setUp() throws Exception {
        _store = createBackingStore();
        _source = createCodePointSource(_store);
    }

    @After
    public void tearDown() throws Exception {
        _source = null;
        _store = null;
    }

    @Test
    public void testBasicRead() throws Exception {
        
        push("bar");

        assertStringRead("bar");

        assertEndOfStream();
    }
    
    @Test
    public void testSplitReads() throws Exception {        
        push("foob");

        assertStringRead("foo");

        push("ar");

        assertStringRead("bar");

        assertEndOfStream();
    }
    
    protected void assertStringRead(String text) throws IOException {
        PrimitiveIterator.OfInt iter = text.codePoints().iterator();
        int index = 0;

        while (iter.hasNext()) {
            int cp = iter.nextInt();
            assertTrue("has no next before <<" + text + ">> @" + index, 
                    _source.hasNext());
            _source.next();
            assertEquals("char in <<" + text + ">> @" + index,
                    cp, _source.getCodePoint());
            index++;
        }
    }
    
    protected void assertEndOfStream() throws IOException {
        assertFalse("has next at end of stream", _source.hasNext());
    }
    
    // TODO: Test reading non-ASCII chars
    // TODO: Test reading chars not on the the BMP
    // TODO: Test malformed UTF-8 and UTF-16 chars.
    // TODO: Fake an end-of-stream
}

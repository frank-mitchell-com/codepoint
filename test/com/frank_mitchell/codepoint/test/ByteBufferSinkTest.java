/**
 * 
 */
package com.frank_mitchell.codepoint.test;

import com.frank_mitchell.codepoint.CodePointSink;
import com.frank_mitchell.codepoint.spi.ByteBufferSink;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.PrimitiveIterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.Ignore;

/**
 * @author fmitchell
 *
 */
@Ignore("don't understand ByteBuffers yet")
public class ByteBufferSinkTest {

    private CodePointSink _sink;
    private Object _store;
    
    
    protected CodePointSink createSink(Object store) {
        return new ByteBufferSink((ByteBuffer) store, StandardCharsets.UTF_8);
    }

    protected Object createBackingStore() {
        return ByteBuffer.allocate(1000);
    }

    protected String getOutput() {
        return ((ByteBuffer)_store).toString();
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
    public void testPutChars() throws IOException {
        String seq = "this is a test";

        _sink.putChars(seq.toCharArray(), 0, seq.length());

        assertEquals(seq, getOutput());
    }

    @Test
    public void testPutCharSequence() throws IOException {
        String seq = "this is a test";

        _sink.putCharSequence(seq);

        assertEquals(seq, getOutput());
    }

    @Test
    public void testPutCodePoint() throws IOException {
        String seq = "\u3041";

        _sink.putCodePoint(seq.codePointAt(0));

        assertEquals(seq, getOutput());
    }
    
    // TODO: Should test something off the BMP, i.e. > 0x10000
    
    @Test
    public void testPutCodePoints() throws IOException {
        String seq = "\u3041\u3042\u3044\u3043";

        int[] cpa = copyCodePoints(seq);
        
        _sink.putCodePoints(cpa, 0, cpa.length);

        assertEquals(seq, getOutput());
    }

    /**
     * Copy the code points from a String to an array.
     * 
     * @param seq the String to turn into code points
     * @return an array of code points
     */
    protected int[] copyCodePoints(String seq) {
        // I hope there's a better way to do this ...
        int len = seq.codePointCount(0, seq.length());
        int[] result = new int[len];
        int i = 0;

        PrimitiveIterator.OfInt iter = seq.codePoints().iterator();
        while (iter.hasNext()) {
            result[i++] = iter.nextInt();
        }
        return result;
    }
}

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

import com.frank_mitchell.codepoint.CodePointSource;
import com.frank_mitchell.codepoint.spi.ByteBufferSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Ignore;

/**
 *
 * @author fmitchell
 */
@Ignore("don't understand ByteBuffers yet")
public class ByteBufferSourceTest extends CodePointSourceTest {

    @Override
    public Object createBackingStore() {
        ByteBuffer buf = ByteBuffer.allocate(1000);
        return buf;
    }

    @Override
    public CodePointSource createCodePointSource(Object store) throws IOException {
        return new ByteBufferSource((ByteBuffer)store);
    }

    @Override
    public void push(String text) {
        ByteBuffer buf = (ByteBuffer)_store;
        buf.put(text.getBytes());
        buf.flip();
    }
}

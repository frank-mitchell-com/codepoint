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
package com.frank_mitchell.codepoint.spi;

import com.frank_mitchell.codepoint.CodePointSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import com.frank_mitchell.codepoint.ForCharsets;

/**
 * A wrapper for a stream of ASCII or UTF-8 bytes.
 * It's fastest if the stream is pure ASCII, but it can handle multi-byte
 * UTF-8 characters correctly, if not efficiently.
 */
class AsciiSource implements CodePointSource {
    private final InputStream _input;
    private int _current = -1;
    private int _next = -1;

    /**
     * Create a source around a stream of ASCII or UTF-8 bytes (only).
     *
     * @param in the Input Stream.
     */
    @ForCharsets(names={"ASCII","UTF-8"})
    public AsciiSource(InputStream in) {
        // TODO: require in != null
        _input = in;
    }

    @Override
    public int getCodePoint() {
        synchronized (this) {
            if (_current < 0) {
                throw new IllegalStateException("have not called next() yet");
            }
            return _current;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        synchronized (this) {
            if (_next < 0) {
                _next = _input.read();
            }
            return _next >= 0;
        }
    }

    @Override
    public void next() throws IOException {
        synchronized (this) {
            if (_next >= 0) {
                _current = _next;
                _next = -1;
            } else {
                _current = _input.read();
            }
            if (_current > 0x7F) {
                int[] result = getUtf8Bytes(_current);
                _current = result[0];
                _next = result[1];
            }
        }
    }

    private int[] getUtf8Bytes(int initial) throws IOException {
        // TODO: require initial > 0
        byte[] buffer = new byte[8];
        int length = 0;
        int b = initial;
        do {
            if (length > buffer.length) {
                buffer = Arrays.copyOf(buffer, length * 2);
            }
            buffer[length] = (byte) b;
            length++;
            b = _input.read();
        } while (b > 0b10000000 && b < 0b10111111);

        // A real programmer would translate the bytes by hand, but ...
        String s = new String(buffer, StandardCharsets.UTF_8);
        int cp = s.codePointAt(0);

        return new int[]{cp, b};
    }

    @Override
    public void close() throws IOException {
        _input.close();
    }
}

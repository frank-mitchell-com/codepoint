package com.frank_mitchell.codepoint.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@link Sink} that wraps a {@link Writer}.
 *
 * @author Frank Mitchell
 */
public class WriterSink extends AbstractSink {

    private final Writer _writer;

    /**
     * Wrap this object around a writer.
     *
     * @param writer the writer
     */
    public WriterSink(Writer writer) {
        _writer = writer;
    }

    /**
     * Wrap this object around a UTF-8 output stream.
     *
     * @param os the output stream
     */
    public WriterSink(OutputStream os) {
        this(os, StandardCharsets.UTF_8);
    }

    /**
     * Wrap this object around an arbitrarily encoded output stream.
     *
     * @param os the output stream
     * @param cs the character set for outgoing bytes
     */
    public WriterSink(OutputStream os, Charset cs) {
        this(new OutputStreamWriter(os, cs));
    }

    private Object getLock() {
        return this;
    }

    @Override
    public void putCodePoint(int cp) throws IOException {
        synchronized (getLock()) {
            if (cp <= 0xFFFF) {
                _writer.write(cp);
            } else {
                // TODO: should we worry about endianness?
                _writer.write(Character.highSurrogate(cp));
                _writer.write(Character.lowSurrogate(cp));
            }
        }
    }

    @Override
    public void flush() throws IOException {
        _writer.flush();
    }

    @Override
    public void close() throws IOException {
        _writer.close();
    }

}

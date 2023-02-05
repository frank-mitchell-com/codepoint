package com.frank_mitchell.codepoint.spi;

import java.io.IOException;
import java.util.PrimitiveIterator;

import com.frank_mitchell.codepoint.CodePointSink;

public abstract class AbstractSink implements CodePointSink {

    @Override
    public void putChars(final char[] cbuf, final int offset, final int len) throws IOException {
        putCharSequence(new CharWrapper(len, cbuf, offset));
    }

    @Override
    public Appendable append(char c) throws IOException {
        // TODO: keep a buffer so we can detect surrogates?
        this.putCodePoint(c);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        this.putCharSequence(csq);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        this.putCharSequence(csq.subSequence(start, end));
        return this;
    }

    @Override
    public void putCharSequence(CharSequence seq) throws IOException {
        PrimitiveIterator.OfInt iter
            = seq.codePoints().iterator();
        while (iter.hasNext()){
            putCodePoint(iter.nextInt());
        }
    }

}
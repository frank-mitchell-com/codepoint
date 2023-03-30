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
import com.frank_mitchell.codepoint.ForCharsets;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author fmitchell
 */
public final class Provider implements CodePointProvider {

    private static final Class<Charset> CHARSET_CLASS = Charset.class;

    /**
     * Map of an input class to a CodePointSource implementation.
     */
    private final Map<Class<?>, Set<ConstructorRecord<CodePointSource>>> _sourcesByClass = new ConcurrentHashMap<>();
    /**
     * Map of an output class to a CodePointSink implementation.
     */
    private final Map<Class<?>, Set<ConstructorRecord<CodePointSink>>> _sinksByClass = new ConcurrentHashMap<>();
    /**
     * Rules to map a Charset to Charsets they extend.
     */
    private final Map<Charset, Set<Charset>> _implies = new ConcurrentHashMap<>();

    private static Set<Charset> set(Charset... charsets) {
        return new CopyOnWriteArraySet<>(Arrays.asList(charsets));
    }

    public Provider() {
        initImpliesTable();
        initSourcesAndSinksTables();
    }

    private void initImpliesTable() {
        // Common knowledge that some charsets are strict subsets of others.
        // E.g. UTF-8 => ASCII and UTF_16BE => UTF_16
        _implies.put(StandardCharsets.UTF_8, set(StandardCharsets.US_ASCII));
        _implies.put(StandardCharsets.ISO_8859_1, set(StandardCharsets.US_ASCII));
        // Not sure which way this one should go, since UTF_16 should handle both "endian" variants
        _implies.put(StandardCharsets.UTF_16BE, set(StandardCharsets.UTF_16));
        _implies.put(StandardCharsets.UTF_16LE, set(StandardCharsets.UTF_16));
        _implies.put(StandardCharsets.UTF_16, set(StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE));
    }

    private void initSourcesAndSinksTables() {
        //collectConstructors(AsciiSource.class, _sourcesByClass);
        //collectConstructors(ByteBufferSource.class, _sourcesByClass);
        collectConstructors(CharSequenceSource.class, _sourcesByClass);
        collectConstructors(ReaderSource.class, _sourcesByClass);

        //collectConstructors(ByteBufferSink.class, _sinksByClass);
        //collectConstructors(StringBufferSink.class, _sinksByClass);
        collectConstructors(WriterSink.class, _sinksByClass);
    }

    private <W, T> W createWrapper(Constructor<? extends W> cons, T obj, Charset cs) throws IOException {
        Class<?>[] types = cons.getParameterTypes();
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            // TODO: keep track of which arguments have been used
            if (types[i].isAssignableFrom(obj.getClass())) {
                args[i] = obj;
            } else if (types[i].isAssignableFrom(cs.getClass())) {
                args[i] = cs;
            } else {
                throw new IllegalStateException("Constructor " + cons + " cannot be called with arguments (" + obj + ", " + cs + ")");
            }
        }
        try {
            return cons.newInstance(args);
        } catch (InstantiationException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings(value = "unchecked")
    private <S, W> void collectConstructors(final Class<?> klass, final Map<Class<?>, Set<ConstructorRecord<S>>> classmap) throws SecurityException {
        Constructor<?>[] conslist = klass.getConstructors();
        for (Constructor<?> cons : conslist) {
            Set<Charset> css = getCharsetsForConstructor(cons);
            ConstructorRecord<?> rec = new ConstructorRecord<>(cons, Collections.unmodifiableSet(css));
            // Fill in constructor maps
            for (Class<?> param : cons.getParameterTypes()) {
                if (!param.equals(CHARSET_CLASS)) {
                    Set<ConstructorRecord<S>> set = classmap.get(param);
                    if (set == null) {
                        set = new CopyOnWriteArraySet<>();
                        classmap.put(param, set);
                    }
                    set.add((ConstructorRecord<S>) rec);
                }
            }
        }
    }

    private boolean matchesCharset(Set<Charset> charsets, Charset cs) {
        if (charsets.contains(cs)) {
            return true;
        }
        for (Charset tcs : charsets) {
            Set<Charset> implied = _implies.get(tcs);
            if (implied != null && implied.contains(cs)) {
                return true;
            }
        }
        return false;
    }

    private void log(String where, String name, Exception e) {
        Logger log = LogManager.getLogManager().getLogger(this.getClass().getName());
        Level level = Level.WARNING;
        if (log.isLoggable(level)) {
            log.log(level, where + " " + name, e);
        }
    }

    private Set<Charset> getCharsetsForConstructor(Constructor<?> cons) {
        Set<Charset> css = new HashSet<>();
        final ForCharsets[] anns = cons.getAnnotationsByType(ForCharsets.class);
        if (anns != null) {
            for (ForCharsets an : anns) {
                String[] names = an.names();
                for (String n : names) {
                    Charset cs = Charset.forName(n);
                    if (cs != null) {
                        css.add(cs);
                    }
                }
            }
        }
        return css;
    }

    private <S, T> Constructor<? extends S> getConstructor(Map<Class<?>, Set<ConstructorRecord<S>>> map, Charset cs, Class<T> type) throws IOException {
        Set<ConstructorRecord<S>> recset = map.get(type);
        if (recset == null) {
            // What *do* we do if we have nothing?
            return null;
        }
        ConstructorRecord<S> first = null;
        for (ConstructorRecord<S> rec : recset) {
            if (first == null) {
                first = rec;
            }
            // First prefer constructors specializing in the target charset
            if (matchesCharset(rec.getCharsets(), cs)) {
                Class<?>[] sig = rec.getConstructor().getParameterTypes();
                if (sig.length == 1 && sig[0].isAssignableFrom(type)) {
                    return rec.getConstructor();
                }
            }

            // Otherwise, prefer the simplest one for the type and charset,
            // or at least the type
            Class<?>[] sig = rec.getConstructor().getParameterTypes();
            if (sig.length == 2 && sig[0].isAssignableFrom(type) && sig[1].equals(CHARSET_CLASS)) {
                return rec.getConstructor();
            } else if (sig.length == 1 && sig[0].isAssignableFrom(type)) {
                return rec.getConstructor();
            }
        }
        // Well, just pick one
        return first == null ? null : first.getConstructor();
    }

    @Override
    public <T> CodePointSource getSource(Class<T> clz, T in, Charset cs) throws IOException {
        Objects.requireNonNull(clz);
        Objects.requireNonNull(in);
        Objects.requireNonNull(cs);

        if (!_sourcesByClass.containsKey(clz)) {
            // is this the right loader?
            readConfiguration(clz.getClassLoader());
        }
        Constructor<? extends CodePointSource> cons = getConstructor(_sourcesByClass, cs, clz);
        if (cons == null) {
            // exception or null??
            throw new IllegalStateException("Constructor " + cons + " cannot be called with arguments (" + in + ", " + cs + ")");
        } else {
            return createWrapper(cons, in, cs);
        }
    }

    /**
     *
     * @param obj the value of obj
     * @param cs the value of cs
     * @throws IOException
     */
    @Override
    public CodePointSource getSource(InputStream obj, Charset cs) throws IOException {
        return new ReaderSource(obj, cs);
    }

    @Override
    public CodePointSource getSource(Reader obj, Charset cs) throws IOException {
        return new ReaderSource(obj, cs);
    }

    @Override
    public CodePointSource getSource(CharSequence obj, Charset cs) throws IOException {
        // assume cs is "UTF-16" or "UTF-16BE"
        return new CharSequenceSource(obj);
    }

    @Override
    public <T> CodePointSink getSink(Class<T> clz, T out, Charset cs) throws IOException {
        Objects.requireNonNull(clz);
        Objects.requireNonNull(out);
        Objects.requireNonNull(cs);

        if (!_sourcesByClass.containsKey(clz)) {
            // is this the right loader?
            readConfiguration(clz.getClassLoader());
        }
        Constructor<? extends CodePointSink> cons = getConstructor(_sinksByClass, cs, clz);
        if (cons == null) {
            // exception or null??
            throw new IllegalStateException("Constructor " + cons + " cannot be called with arguments (" + out + ", " + cs + ")");
        } else {
            return createWrapper(cons, out, cs);
        }
    }

    @Override
    public CodePointSink getSink(OutputStream obj, Charset cs) throws IOException {
        return new WriterSink(obj, cs);
    }

    @Override
    public CodePointSink getSink(Writer obj, Charset cs) throws IOException {
        return new WriterSink(obj, cs);
    }

    private void readConfiguration(ClassLoader loader) throws SecurityException, IOException {
        InputStream instream = loader.getResourceAsStream(CodePointProvider.CONFIG_FILE);
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(instream, StandardCharsets.UTF_8));
        String line = reader.readLine();
        while (line != null) {
            int comment = line.indexOf('#');
            if (comment > 0) {
                line = line.substring(0, comment);
            }
            String[] names = line.split("\\s+");
            for (String s : names) {
                if (s.length() == 0) {
                    continue;
                }
                try {
                    Class<?> clz = loader.loadClass(s);
                    if (CodePointSource.class.isAssignableFrom(clz)) {
                        collectConstructors(clz, _sourcesByClass);
                    }
                    if (CodePointSink.class.isAssignableFrom(clz)) {
                        collectConstructors(clz, _sinksByClass);
                    }
                } catch (ClassNotFoundException e) {
                    log("Loading", s, e);
                }
            }
            line = reader.readLine();
        }
    }
}

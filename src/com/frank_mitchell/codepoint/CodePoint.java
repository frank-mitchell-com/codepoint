package com.frank_mitchell.codepoint;

import com.frank_mitchell.codepoint.spi.*;
import java.io.File;
import java.io.FileWriter;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Determines a {@link CodePointSource} or {@link CodePointSink} for a given
 * object.
 *
 * It uses the {@link ClassLoader}, configuration files, reflection, and a few
 * heuristics to instantiate an appropriate class. Other implementers of
 * {@link CodePointSource} and {@link CodePointSink} merely need to include in
 * the jar a UTF-8 file named {@code META-INF/codepoint/classes.conf} containing
 * the fully qualified binary names of classes implementing
 * {@link CodePointSource} or {@link CodePointSink}, separated only by
 * whitespace or comments ('#' until the end of the line).
 *
 * Sources and sinks that can only handle a restricted range of {@link Charset}s
 * must indicate which sets with {@link ForCharset} on the relevant
 * constructor(s).
 *
 * @author Frank Mitchell
 */
public class CodePoint {

    public static final String CONFIG_DIR = "/META-INF/codepoint";
    public static final String SOURCES_FILE = CONFIG_DIR + "/sources.conf";
    public static final String SINKS_FILE = CONFIG_DIR + "/sinks.conf";
    
    public static final Class<Charset> CHARSET_CLASS = Charset.class;
    
    private static class ConstructorRecord<S> {
        Constructor<? extends S> _cons;
        Set<Charset> _charsets;
        
        ConstructorRecord(Constructor<? extends S> c, Set<Charset> css) {
            _cons = c;
            _charsets = css;
        }
        
        Constructor<? extends S> getConstructor() {
            return _cons;
        }
        
        Set<Charset> getCharsets() {
            return _charsets;
        }
        
        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("('").append(_cons).append("' ").append(_charsets).append(")");
            return b.toString();
        }
    }
    
    /**
     * Map of an input class to a CodePointSource implementation.
     */
    private final static Map<Class<?>, Set<ConstructorRecord<CodePointSource>>> _sourcesByClass = new ConcurrentHashMap<>();

    /**
     * Map of an output class to a CodePointSink implementation.
     */
    private final static Map<Class<?>, Set<ConstructorRecord<CodePointSink>>> _sinksByClass = new ConcurrentHashMap<>();

    /**
     * Rules to map a Charset to Charsets they extend.
     */
    private final static Map<Charset, Set<Charset>> _implies = new ConcurrentHashMap<>();

    static {
        // Common knowledge that some charsets are strict subsets of others.
        // E.g. UTF-8 => ASCII and UTF_16BE => UTF_16
        _implies.put(StandardCharsets.UTF_8, set(StandardCharsets.US_ASCII));
        _implies.put(StandardCharsets.ISO_8859_1, set(StandardCharsets.US_ASCII));
        
        // Not sure which way this one should go, since UTF_16 should handle both "endian" variants
        _implies.put(StandardCharsets.UTF_16BE, set(StandardCharsets.UTF_16));
        _implies.put(StandardCharsets.UTF_16LE, set(StandardCharsets.UTF_16));
        _implies.put(StandardCharsets.UTF_16, 
                set(StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE));
        
        // Some seed values
        try {
            readConfiguration(ClassLoader.getSystemClassLoader());
        } catch (IOException e) {
            
        }
    }

    private static Set<Charset> set(Charset... charsets) {
        return new CopyOnWriteArraySet<>(Arrays.asList(charsets));
    }

    public static <T> CodePointSource getSource(Class<T> clz, T obj, Charset cs) throws IOException {
        Constructor<? extends CodePointSource> cons
                = getConstructor(CodePointSource.class, _sourcesByClass, cs, clz);
        if (cons == null) {
            // exception or null??
            throw new IllegalStateException("Constructor " + cons
                    + " cannot be called with arguments ("
                    + obj + ", " + cs + ")");
        } else {
            return createWrapper(cons, obj, cs);
        }
    }

    protected static <W, T> W createWrapper(Constructor<? extends W> cons, T obj, Charset cs) throws IOException {
        Class<?>[] types = cons.getParameterTypes();
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            // TODO: keep track of which arguments have been used
            if (types[i].isAssignableFrom(obj.getClass())) {
                args[i] = obj;
            } else if (types[i].isAssignableFrom(cs.getClass())) {
                args[i] = cs;
            } else {
                throw new IllegalStateException("Constructor " + cons
                        + " cannot be called with arguments ("
                        + obj + ", " + cs + ")");
            }
        }
        try {
            return cons.newInstance(args);
        } catch (InstantiationException
                | InvocationTargetException
                | IllegalArgumentException
                | IllegalAccessException e) {
            throw new IOException(e);
        }
    }

    private static <S, T> Constructor<? extends S> getConstructor(
            Class<S> stype,
            Map<Class<?>, Set<ConstructorRecord<S>>> map,
            Charset cs, 
            Class<T> type)
            throws IOException {
        if (!map.containsKey(type)) {
            // Read a configuration file for class names
            ClassLoader loader = type.getClassLoader();  // is this the right loader?
            readConfiguration(loader);
        }
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
                return rec.getConstructor();
            }
            
            // Otherwise, prefer the simplest one for the type and charset, 
            // or at least the type
            Class<?>[] sig = rec.getConstructor().getParameterTypes();
            if (sig.length == 2
                && sig[0].isAssignableFrom(type) 
                && sig[1].equals(CHARSET_CLASS)) {
                return rec.getConstructor();
            } else if (sig.length == 1 && sig[0].isAssignableFrom(type)) {
                return rec.getConstructor();
            }
        }
        // Well, just pick one
        return first == null ? null : first.getConstructor();
    }

    private static boolean matchesCharset(Set<Charset> charsets, Charset cs) {
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

    private static void readConfiguration(ClassLoader loader) throws IOException, SecurityException {
        InputStream instream = loader.getResourceAsStream(SOURCES_FILE);
        LineNumberReader reader
                = new LineNumberReader(new InputStreamReader(instream, StandardCharsets.UTF_8));
        String line = reader.readLine();
        while (line != null) {
            int comment = line.indexOf('#');
            if (comment > 0) {
                line = line.substring(0, comment);
            }
            String[] names = line.strip().split("\\s+");
            for (String s : names) {
                try {
                    Class<?> clz = loader.loadClass(s);
                    if (CodePointSource.class.isAssignableFrom(clz)) {
                        collectConstructors(clz, _sourcesByClass);
                    }
                    if (CodePointSink.class.isAssignableFrom(clz)) {
                        collectConstructors(clz, _sinksByClass);
                    }
                } catch (ClassNotFoundException e) {
                    // should log this or something
                }
            }
            line = reader.readLine();
        }
    }

    private static <S,W> void collectConstructors(final Class<?> klass, 
            final Map<Class<?>, Set<ConstructorRecord<S>>> classmap) throws SecurityException {

        try {
            Constructor<?>[] conslist = klass.getConstructors();
            for (Constructor<?> cons : conslist) {
                Set<Charset> css = getCharsetsForConstructor(cons);
                ConstructorRecord<?> rec = 
                        new ConstructorRecord<>(cons, Collections.unmodifiableSet(css));
                
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
        } catch (SecurityException e) {
            // we should log this ...
        }
    }

    private static Set<Charset> getCharsetsForConstructor(Constructor<?> cons) {
        Set<Charset> css = new HashSet<>();
        final ForCharsets[] anns =
                cons.getAnnotationsByType(ForCharsets.class);
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

    public static CodePointSource getSource(InputStream obj, Charset cs) throws IOException {
        return new ReaderSource(obj, cs);
    }

    public static CodePointSource getSource(Reader obj, Charset cs) throws IOException {
        return new ReaderSource(obj, cs);
    }

    public static <T> CodePointSink getSink(Class<T> clz, T obj, Charset cs) throws IOException {
        Constructor<? extends CodePointSink> cons
                = getConstructor(CodePointSink.class, _sinksByClass, cs, clz);
        if (cons == null) {
            // exception or null??
            throw new IllegalStateException("Constructor " + cons
                    + " cannot be called with arguments ("
                    + obj + ", " + cs + ")");
        } else {
            return createWrapper(cons, obj, cs);
        }
    }

    public static CodePointSink getSink(OutputStream obj, Charset cs) throws IOException {
        return new WriterSink(obj, cs);
    }

    public static CodePointSink getSink(Writer obj, Charset cs) throws IOException {
        return new WriterSink(obj, cs);
    }

    public void dumpMappings(File file) throws IOException {
        // TODO: find a real format for this: JSON, YAML, SLAN, something.
        HashMap<String, Object> tmpmap = new HashMap<>();
        tmpmap.put("sources", _sourcesByClass);
        tmpmap.put("sinks", _sinksByClass);
        tmpmap.put("implies", _implies);

        Writer writer = new FileWriter(file);
        writer.write(tmpmap.toString());
        writer.close();
    }
}

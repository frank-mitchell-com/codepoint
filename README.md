## Introduction

**Codepoint** turns other Java APIs into a consistent pair of APIs for
processing Unicode code points.  I originally wrote these classes for
[**jsonpp**](https://github.com/frank-mitchell-com/jsonpp), but
I thought they'd be generally useful.  For me, at least.

Java 17 or 19 may have solved this problem. As I'm working in Java 8 or 9
for reasons I can't use them. (Meaning I don't know what they are. I'm still
catching up.)


## Building Codepoint

The project includes a project file for NetBeans. If you use Eclipse,
`ant -f nbbuild.xml' also works from the command line.

There's also a non-functional 'build.xml'. Don't use that (yet).

It's a bunch of `.java` files. It's not that hard.


## Installing Codepoint

Simply put codepoint.jar (or codepoint-*version*.jar) in your CLASSPATH.


## Using Codepoint

The `com.frank_mitchell.codepoint` package includes the public API,
while `com.frank_mitchell.codepoint.spi` contains the implemenation(s).

### `CodePointSource`

As it was created to parse JSON, this class only has four methods,
used thusly.

    CodePointSource source;

...

    if (source.hasNext()) {
        source.next();
        int c = source.getCodePoint();
        // do something with the code point
...
    }
    source.close();

Unlike `Iterator`s I separate out the step to advance iteration and
the step to get the (current) item in the iteration.  It's a pattern with me
... but one which allows you to pass a `CodePointSource` to other methods
so they can see the last code point parsed and decide how to proceed,
rather than pushing that character back onto the stream for the next guy
as many similar APIs do.

Current implementations wrap other Java classes, including
`CharSequence` and `Reader`.
A `ByteBuffer` implementation is in the works.


### `CodePointSink`

This class offers a selection of methods to write code points, including:

- One `int` at a time.
- Through the `Appendable` interface.
- As an `IntStream`, which is how  Appendable` does it.

It also has the obligatory `flush()` and `close()` methods, like any
`Writer` or `OutputStream`.

The current implementation wraps a `Writer`.
A `ByteBuffer` implementation is in the works.

Most of these methods have default implementations so a new `CodePoint`
implementation needs only implement 
`putCodePoint(int)`, `flush()`, and `close()`.
The `char`-methods all translate UTF-16 sequences to code points.


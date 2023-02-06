## Build

- Fix build.xml

## Interface

- Add a "factory" or two to select a CodePointSource or CodePointSink
  based on the type of standard Java input or output type?
  All the cool Java programmers are doing it.

- Add more bulk read methods to CodePointSource, implemented through
  `default` methods and/or an abstract class.

## Implementation

- Get ByteBufferSource and ByteBufferSink working, once I understand
  ByteBuffers better.

- Add Sources and Sinks for CharBuffers and IntBuffers?

- Move/copy the Utf8Source from **jsonpp** to this project, once it's
  working.

- Look at Java 17 and Java 19 for additional input and output classes.
  (Maybe some that makes this whole library obsolete?)

## Testing

- Add more tests for possible hiccups and error conditions.


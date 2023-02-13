## Build

- Fix build.xml

## Interface

- Add more bulk read methods to CodePointSource, implemented through
  `default` methods and/or an abstract class.

- CodePointSequence interface, parallel to CharSequence?

## Implementation

- Implement the service and reflection code for CodePoint.
  Right now it's just hardcoded to serve JSONPP.

- Get ByteBufferSource and ByteBufferSink working, once I understand
  ByteBuffers better.

- Add Sources and Sinks for CharBuffers and IntBuffers?

- Implement CodePointSequence interface if it exists.

- Look at Java 17 and Java 19 for additional input and output classes.
  (Maybe some that makes this whole library obsolete?)

## Testing

- Add more tests for possible hiccups and error conditions.


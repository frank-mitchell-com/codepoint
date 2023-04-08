## Interface

- Add more bulk read methods to CodePointSource, implemented through
  `default` methods and/or an abstract class.

- CodePointSequence interface, parallel to CharSequence?

## Implementation

- Get ByteBufferSource and ByteBufferSink working, once I understand
  ByteBuffers better.

- Load existing implementations and charset mappings from config file.

- Get config files from other jars/classloaders for new sources and sinks.

- Load CodePointProvider as a service.
  - Move class loading machinery elsewhere?

## Testing

- Add more tests for possible hiccups and error conditions.

## Documentation

- Sample code of CodePointSource.

- More documentation and examples of CodePointSink.


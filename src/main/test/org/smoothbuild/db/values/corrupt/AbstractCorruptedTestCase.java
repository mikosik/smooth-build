package org.smoothbuild.db.values.corrupt;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public abstract class AbstractCorruptedTestCase extends TestingContext {
  protected Hash hash(String string) throws IOException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value ? 1 : 0);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws IOException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.write(bytes);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(Value value) {
    return value.hash();
  }

  protected Hash hash(Hash... hashes) throws IOException {
    return hashedDb().writeHashes(hashes);
  }
}

package org.smoothbuild.db.values.corrupt;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.testing.TestingContext;

import com.google.common.hash.HashCode;

import okio.ByteString;

public abstract class AbstractCorruptedTestCase extends TestingContext {
  protected HashCode hash(String string) throws IOException {
    return hashedDb().writeString(string);
  }

  protected HashCode hash(boolean value) throws IOException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value ? 1 : 0);
      sink.close();
      return sink.hash();
    }
  }

  protected HashCode hash(ByteString bytes) throws IOException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.write(bytes);
      sink.close();
      return sink.hash();
    }
  }

  protected HashCode hash(Value value) {
    return value.hash();
  }

  protected HashCode hash(HashCode... hashes) throws IOException {
    return hashedDb().writeHashes(hashes);
  }
}

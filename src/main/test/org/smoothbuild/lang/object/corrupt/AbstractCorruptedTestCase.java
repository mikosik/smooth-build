package org.smoothbuild.lang.object.corrupt;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public abstract class AbstractCorruptedTestCase extends TestingContext {
  protected Hash hash(String string) throws IOException {
    return valuesDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value ? 1 : 0);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.write(bytes);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(SObject object) {
    return object.hash();
  }

  protected Hash hash(Hash... hashes) throws IOException {
    return valuesDb().writeHashes(hashes);
  }
}

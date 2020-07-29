package org.smoothbuild.db.record.corrupt;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public abstract class AbstractCorruptedTestCase extends TestingContext {
  protected Hash hash(String string) throws HashedDbException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException, HashedDbException {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value);
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

  protected Hash hash(Record record) {
    return record.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeHashes(hashes);
  }
}

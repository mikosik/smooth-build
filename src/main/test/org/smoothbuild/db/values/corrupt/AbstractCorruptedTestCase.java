package org.smoothbuild.db.values.corrupt;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

import okio.ByteString;

public abstract class AbstractCorruptedTestCase {
  protected HashedDb hashedDb;
  protected ValuesDb valuesDb;

  @Before
  public void before() {
    hashedDb = new TestingHashedDb();
    valuesDb = new ValuesDb(hashedDb);
  }

  protected HashCode bool(boolean value) {
    return valuesDb.bool(value).hash();
  }

  protected HashCode string(String string) {
    return valuesDb.string(string).hash();
  }

  protected HashCode hash(String string) throws IOException {
    return hashedDb.writeString(string);
  }

  protected HashCode hash(boolean value) throws IOException {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.sink().writeByte(value ? 1 : 0);
      marshaller.close();
      return marshaller.hash();
    }
  }

  protected HashCode hash(ByteString bytes) throws IOException {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.sink().write(bytes);
      marshaller.close();
      return marshaller.hash();
    }
  }

  protected HashCode hash(ConcreteType type) {
    return type.hash();
  }

  protected HashCode hash(HashCode... hashes) throws IOException {
    return hashedDb.writeHashes(hashes);
  }
}

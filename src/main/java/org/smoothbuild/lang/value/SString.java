package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public class SString extends Value {
  private final HashedDb hashedDb;

  public SString(Type type, HashCode hash, HashedDb hashedDb) {
    super(type, hash);
    checkArgument(type.name().equals("String"));
    this.hashedDb = checkNotNull(hashedDb);
  }

  public static SString storeStringInDb(Type type, String string, HashedDb hashedDb) {
    Marshaller marshaller = hashedDb.newMarshaller();
    marshaller.write(string.getBytes(CHARSET));
    marshaller.close();
    return new SString(type, marshaller.hash(), hashedDb);
  }

  public String value() {
    try {
      return inputStreamToString(hashedDb.newUnmarshaller(hash()));
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while reading " + hash() + " value.");
    }
  }

  @Override
  public String toString() {
    return value();
  }
}

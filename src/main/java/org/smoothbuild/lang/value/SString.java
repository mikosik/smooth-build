package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;

import com.google.common.hash.HashCode;

public class SString extends Value {
  private final HashedDb hashedDb;

  public SString(HashCode hash, HashedDb hashedDb) {
    super(STRING, hash);
    this.hashedDb = checkNotNull(hashedDb);
  }

  public static SString storeStringInDb(String string, HashedDb hashedDb) {
    byte[] bytes = string.getBytes(CHARSET);
    HashCode hash = hashedDb.write(bytes);
    return new SString(hash, hashedDb);
  }

  public String value() {
    try {
      return inputStreamToString(hashedDb.openInputStream(hash()));
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while reading " + hash() + " value.");
    }
  }

  @Override
  public String toString() {
    return value();
  }
}

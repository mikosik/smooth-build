package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.err.ReadingHashedObjectFailedError;
import org.smoothbuild.lang.base.SString;

import com.google.common.hash.HashCode;

public class StringObject extends AbstractObject implements SString {
  private final HashedDb hashedDb;

  public StringObject(HashedDb hashedDb, HashCode hash) {
    super(STRING, hash);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public String value() {
    try {
      return inputStreamToString(hashedDb.openInputStream(hash()));
    } catch (IOException e) {
      throw new ReadingHashedObjectFailedError(hash(), e);
    }
  }

  @Override
  public String toString() {
    return value();
  }
}

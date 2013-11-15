package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.err.ReadingHashedObjectFailedError;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.hash.HashCode;

public class CachedString implements StringValue {
  private final HashedDb hashedDb;
  private final HashCode hash;

  public CachedString(HashedDb hashedDb, HashCode hash) {
    this.hashedDb = checkNotNull(hashedDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Type type() {
    return Type.STRING;
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public String value() {
    try {
      return inputStreamToString(hashedDb.openInputStream(hash));
    } catch (IOException e) {
      throw new ErrorMessageException(new ReadingHashedObjectFailedError(hash, e));
    }
  }
}

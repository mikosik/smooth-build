package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.function.base.Type.STRING;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.err.ReadingHashedObjectFailedError;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.hash.HashCode;

public class CachedString extends AbstractValue implements StringValue {
  private final HashedDb hashedDb;

  public CachedString(HashedDb hashedDb, HashCode hash) {
    super(STRING, hash);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public String value() {
    try {
      return inputStreamToString(hashedDb.openInputStream(hash()));
    } catch (IOException e) {
      throw new ErrorMessageException(new ReadingHashedObjectFailedError(hash(), e));
    }
  }
}

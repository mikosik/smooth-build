package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.err.ReadingHashedObjectFailedError;
import org.smoothbuild.plugin.StringValue;

import com.google.common.hash.HashCode;

public class StringObject implements StringValue {
  private final HashedDb hashedDb;
  private final HashCode hash;

  public StringObject(HashedDb hashedDb, HashCode hash) {
    this.hashedDb = checkNotNull(hashedDb);
    this.hash = checkNotNull(hash);
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

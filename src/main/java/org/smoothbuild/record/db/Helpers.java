package org.smoothbuild.record.db;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;

public class Helpers {
  public static <T> T wrapException(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  public static <T> T wrapException(Hash hash, HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash, e);
    }
  }

  @FunctionalInterface
  public static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }
}

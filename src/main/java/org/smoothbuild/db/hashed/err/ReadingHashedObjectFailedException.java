package org.smoothbuild.db.hashed.err;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;

public class ReadingHashedObjectFailedException extends HashedDbException {
  public ReadingHashedObjectFailedException(HashCode hash, Exception e) {
    super("IO error occurred while reading " + hash + " object.\n"
        + Throwables.getStackTraceAsString(e));
  }
}

package org.smoothbuild.object.err;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;

public class ReadingHashedObjectFailedError extends HashedDbError {
  public ReadingHashedObjectFailedError(HashCode hash, Exception e) {
    super("IO error occurred while reading " + hash + " object.\n"
        + Throwables.getStackTraceAsString(e));
  }
}

package org.smoothbuild.object.err;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;

public class ReadingHashedObjectFailedError extends ObjectDbError {
  public ReadingHashedObjectFailedError(HashCode hash, Exception e) {
    super("IO error occurred while reading object with hash = " + hash + "\n"
        + Throwables.getStackTraceAsString(e));
  }
}

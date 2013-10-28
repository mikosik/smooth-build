package org.smoothbuild.object.err;

import java.io.IOException;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;

public class WritingHashedObjectFailedError extends HashedDbError {
  public WritingHashedObjectFailedError(HashCode hash, IOException e) {
    super("IO error occurred while writing " + hash + " object.\n"
        + Throwables.getStackTraceAsString(e));
  }
}

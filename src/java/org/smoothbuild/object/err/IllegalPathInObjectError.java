package org.smoothbuild.object.err;

import com.google.common.hash.HashCode;

public class IllegalPathInObjectError extends ObjectDbError {
  public IllegalPathInObjectError(HashCode hash, String message) {
    super(hash.toString() + " object contains illegal path: " + message);
  }
}

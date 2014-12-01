package org.smoothbuild.db.hashed.err;

import com.google.common.hash.HashCode;

public class IllegalPathInObjectError extends HashedDbError {
  public IllegalPathInObjectError(HashCode hash, String message) {
    super(hash.toString() + " object contains illegal path: " + message);
  }
}

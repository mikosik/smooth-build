package org.smoothbuild.db.hashed.err;

import com.google.common.hash.HashCode;

public class IllegalPathInObjectException extends HashedDbException {
  public IllegalPathInObjectException(HashCode hash, String message) {
    super(hash.toString() + " object contains illegal path: " + message);
  }
}

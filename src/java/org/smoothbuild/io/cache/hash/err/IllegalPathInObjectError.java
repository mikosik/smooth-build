package org.smoothbuild.io.cache.hash.err;

import com.google.common.hash.HashCode;

@SuppressWarnings("serial")
public class IllegalPathInObjectError extends HashedDbError {
  public IllegalPathInObjectError(HashCode hash, String message) {
    super(hash.toString() + " object contains illegal path: " + message);
  }
}

package org.smoothbuild.io.db.hash.err;

import com.google.common.hash.HashCode;

public class NoObjectWithGivenHashError extends HashedDbError {
  public NoObjectWithGivenHashError(HashCode hash) {
    super("Could not find " + hash + " object.");
  }
}

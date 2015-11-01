package org.smoothbuild.db.hashed.err;

import com.google.common.hash.HashCode;

public class NoObjectWithGivenHashException extends HashedDbException {
  public NoObjectWithGivenHashException(HashCode hash) {
    super("Could not find " + hash + " object.");
  }
}

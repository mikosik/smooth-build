package org.smoothbuild.object.err;

import com.google.common.hash.HashCode;

public class NoObjectWithGivenHashError extends ObjectDbError {
  public NoObjectWithGivenHashError(HashCode hash) {
    super("Could not find object with hash = " + hash);
  }
}

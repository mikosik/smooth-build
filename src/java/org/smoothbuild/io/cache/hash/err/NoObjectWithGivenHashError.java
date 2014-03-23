package org.smoothbuild.io.cache.hash.err;

import com.google.common.hash.HashCode;

@SuppressWarnings("serial")
public class NoObjectWithGivenHashError extends HashedDbError {
  public NoObjectWithGivenHashError(HashCode hash) {
    super("Could not find " + hash + " object.");
  }
}

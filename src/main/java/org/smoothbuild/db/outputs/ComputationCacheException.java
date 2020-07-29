package org.smoothbuild.db.outputs;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;

public class ComputationCacheException extends Exception {
  public static ComputationCacheException corruptedValueException(Hash hash, String message) {
    return new ComputationCacheException(
        hash.toString() + " value in OutputDb is corrupted. " + message);
  }

  public static ComputationCacheException outputDbException(IOException e) {
    return new ComputationCacheException("IOException when accessing OutputDb", e);
  }

  private ComputationCacheException(String message) {
    super(message);
  }

  private ComputationCacheException(String message, Throwable e) {
    super(message, e);
  }
}

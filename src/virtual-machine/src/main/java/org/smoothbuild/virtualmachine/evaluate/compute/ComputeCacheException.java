package org.smoothbuild.virtualmachine.evaluate.compute;

import java.io.IOException;
import org.smoothbuild.common.base.Hash;

public class ComputeCacheException extends Exception {
  public static ComputeCacheException corruptedValueException(Hash hash, String message) {
    return new ComputeCacheException(
        hash.toString() + " value in ComputationCache is corrupted. " + message);
  }

  public static ComputeCacheException computeException(IOException e) {
    return new ComputeCacheException("IOException when accessing ComputationCache", e);
  }

  private ComputeCacheException(String message) {
    super(message);
  }

  private ComputeCacheException(String message, Throwable e) {
    super(message, e);
  }
}

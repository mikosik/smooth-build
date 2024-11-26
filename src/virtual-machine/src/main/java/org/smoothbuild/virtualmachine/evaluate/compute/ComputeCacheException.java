package org.smoothbuild.virtualmachine.evaluate.compute;

import java.io.IOException;
import org.smoothbuild.common.base.Hash;

public class ComputeCacheException extends IOException {
  public static ComputeCacheException corruptedValueException(Hash hash, String message) {
    return new ComputeCacheException(
        hash.toString() + " value in ComputationCache is corrupted. " + message);
  }

  private ComputeCacheException(String message) {
    super(message);
  }
}

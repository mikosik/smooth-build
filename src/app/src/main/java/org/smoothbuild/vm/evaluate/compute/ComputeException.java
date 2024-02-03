package org.smoothbuild.vm.evaluate.compute;

import java.io.IOException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class ComputeException extends Exception {
  public static ComputeException corruptedValueException(Hash hash, String message) {
    return new ComputeException(
        hash.toString() + " value in ComputationCache is corrupted. " + message);
  }

  public static ComputeException computationCacheException(IOException e) {
    return new ComputeException("IOException when accessing ComputationCache", e);
  }

  private ComputeException(String message) {
    super(message);
  }

  private ComputeException(String message, Throwable e) {
    super(message, e);
  }
}

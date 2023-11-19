package org.smoothbuild.vm.evaluate.compute;

import java.io.IOException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class ComputationCacheException extends Exception {
  public static ComputationCacheException corruptedValueException(Hash hash, String message) {
    return new ComputationCacheException(
        hash.toString() + " value in ComputationCache is corrupted. " + message);
  }

  public static ComputationCacheException computationCacheException(IOException e) {
    return new ComputationCacheException("IOException when accessing ComputationCache", e);
  }

  private ComputationCacheException(String message) {
    super(message);
  }

  private ComputationCacheException(String message, Throwable e) {
    super(message, e);
  }
}

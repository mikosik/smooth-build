package org.smoothbuild.vm.compute;

import java.io.IOException;

import org.smoothbuild.bytecode.hashed.Hash;

public class ComputationCacheExc extends Exception {
  public static ComputationCacheExc corruptedValueException(Hash hash, String message) {
    return new ComputationCacheExc(
        hash.toString() + " value in ComputationCache is corrupted. " + message);
  }

  public static ComputationCacheExc computationCacheException(IOException e) {
    return new ComputationCacheExc("IOException when accessing ComputationCache", e);
  }

  private ComputationCacheExc(String message) {
    super(message);
  }

  private ComputationCacheExc(String message, Throwable e) {
    super(message, e);
  }
}

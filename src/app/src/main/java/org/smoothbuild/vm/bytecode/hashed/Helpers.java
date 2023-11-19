package org.smoothbuild.vm.bytecode.hashed;

import java.io.IOException;

import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;

public class Helpers {
  public static void wrapIOExceptionAsHashedDbException(IoRunnable runnable)
      throws HashedDbException {
    try {
      runnable.run();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  @FunctionalInterface
  public static interface IoRunnable {
    public void run() throws IOException;
  }
}

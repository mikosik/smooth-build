package org.smoothbuild.db.hashed;

import java.io.IOException;

import org.smoothbuild.db.hashed.exc.HashedDbExc;

public class Helpers {
  public static void wrapIOExceptionAsHashedDbException(IoRunnable runnable)
      throws HashedDbExc {
    try {
      runnable.run();
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }

  @FunctionalInterface
  public static interface IoRunnable {
    public void run() throws IOException;
  }
}

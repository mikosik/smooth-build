package org.smoothbuild.db;

import java.io.IOException;

import org.smoothbuild.db.exc.HashedDbExc;

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

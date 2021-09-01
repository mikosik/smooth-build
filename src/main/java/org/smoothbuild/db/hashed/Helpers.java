package org.smoothbuild.db.hashed;

import java.io.IOException;

public class Helpers {
  public static void wrapException(IoRunnable runnable) throws HashedDbException {
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
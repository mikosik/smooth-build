package org.smoothbuild.builtin.compress;

import org.smoothbuild.message.Error;

public class IllegalPathInZipError extends Error {
  public IllegalPathInZipError(String fileName) {
    super("File in a zip file has illegal name = '" + fileName + "'");
  }
}

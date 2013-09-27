package org.smoothbuild.builtin.compress.err;

import org.smoothbuild.message.message.Error;

@SuppressWarnings("serial")
public class IllegalPathInZipError extends Error {
  public IllegalPathInZipError(String fileName) {
    super("File in a zip file has illegal name = '" + fileName + "'");
  }
}

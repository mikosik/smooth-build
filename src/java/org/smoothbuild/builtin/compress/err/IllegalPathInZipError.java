package org.smoothbuild.builtin.compress.err;

import org.smoothbuild.message.message.ErrorMessage;

public class IllegalPathInZipError extends ErrorMessage {
  public IllegalPathInZipError(String fileName) {
    super("File in a zip file has illegal name = '" + fileName + "'");
  }
}

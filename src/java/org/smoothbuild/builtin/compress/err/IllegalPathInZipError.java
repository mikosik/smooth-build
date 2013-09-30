package org.smoothbuild.builtin.compress.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class IllegalPathInZipError extends Message {
  public IllegalPathInZipError(String fileName) {
    super(ERROR, "File in a zip file has illegal name = '" + fileName + "'");
  }
}

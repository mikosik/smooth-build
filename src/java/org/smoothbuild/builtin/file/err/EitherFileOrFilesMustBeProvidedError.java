package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class EitherFileOrFilesMustBeProvidedError extends Message {
  public EitherFileOrFilesMustBeProvidedError() {
    super(ERROR, "Either 'file' or 'files' parameter must be provided.");
  }
}

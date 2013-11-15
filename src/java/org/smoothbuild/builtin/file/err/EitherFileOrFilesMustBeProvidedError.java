package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class EitherFileOrFilesMustBeProvidedError extends Message {
  public EitherFileOrFilesMustBeProvidedError() {
    super(ERROR, "Either 'file' or 'files' parameter must be provided.");
  }
}

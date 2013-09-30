package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;

public class EitherFileOrFilesMustBeProvidedError extends ErrorMessage {
  public EitherFileOrFilesMustBeProvidedError() {
    super("Either 'file' or 'files' parameter must be provided.");
  }
}

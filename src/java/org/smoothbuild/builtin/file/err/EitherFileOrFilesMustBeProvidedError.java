package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.Error;

@SuppressWarnings("serial")
public class EitherFileOrFilesMustBeProvidedError extends Error {
  public EitherFileOrFilesMustBeProvidedError() {
    super("Either 'file' or 'files' parameter must be provided.");
  }
}

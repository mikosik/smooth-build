package org.smoothbuild.builtin.file.err;

import org.smoothbuild.problem.Error;

public class EitherFileOrFilesMustBeProvidedError extends Error {
  public EitherFileOrFilesMustBeProvidedError() {
    super("Either 'file' or 'files' parameter must be provided.");
  }
}

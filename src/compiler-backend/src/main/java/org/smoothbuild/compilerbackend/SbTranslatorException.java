package org.smoothbuild.compilerbackend;

import java.io.IOException;

public class SbTranslatorException extends Exception {
  public SbTranslatorException(IOException ioException) {
    this(ioException.getMessage());
  }

  public SbTranslatorException(String message, Throwable e) {
    super(message, e);
  }

  public SbTranslatorException(String message) {
    super(message);
  }
}

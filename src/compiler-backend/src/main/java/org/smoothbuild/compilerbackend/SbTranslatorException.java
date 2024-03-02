package org.smoothbuild.compilerbackend;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public class SbTranslatorException extends Exception {
  public SbTranslatorException(BytecodeException bytecodeException) {
    this(bytecodeException.getMessage());
  }

  public SbTranslatorException(String message, Throwable e) {
    super(message, e);
  }

  public SbTranslatorException(String message) {
    super(message);
  }
}

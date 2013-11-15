package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class CompilerFailedWithoutDiagnosticsError extends Message {
  public CompilerFailedWithoutDiagnosticsError() {
    super(ERROR, "Internal error: Compilation failed but JavaCompiler reported no error message.");
  }
}

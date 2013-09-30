package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class CompilerFailedWithoutDiagnosticsError extends Message {
  public CompilerFailedWithoutDiagnosticsError() {
    super(ERROR, "Internal error: Compilation failed but JavaCompiler reported no error message.");
  }
}

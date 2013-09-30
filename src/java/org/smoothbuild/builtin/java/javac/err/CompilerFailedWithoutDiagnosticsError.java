package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.message.ErrorMessage;

public class CompilerFailedWithoutDiagnosticsError extends ErrorMessage {
  public CompilerFailedWithoutDiagnosticsError() {
    super("Internal error: Compilation failed but JavaCompiler reported no error message.");
  }
}

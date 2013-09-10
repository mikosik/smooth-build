package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.problem.Error;

public class CompilerFailedWithoutDiagnosticsError extends Error {
  public CompilerFailedWithoutDiagnosticsError() {
    super("Internal error: Compilation failed but JavaCompiler reported no error message.");
  }
}

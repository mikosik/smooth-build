package org.smoothbuild.vm.bytecode.expr.exc;

import java.io.IOException;
import org.smoothbuild.vm.bytecode.BytecodeException;

public class IoBytecodeException extends BytecodeException {
  public IoBytecodeException(IOException ioException) {
    super("IOException reading from bytecodeDb: " + ioException.getMessage(), ioException);
  }
}

package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public class IoBytecodeException extends BytecodeException {
  public IoBytecodeException(IOException ioException) {
    super("IOException reading from ExprDb: " + ioException.getMessage(), ioException);
  }
}

package org.smoothbuild.virtualmachine.evaluate.compute;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.plugin.MessageLogger;

public interface ContainerMessageLogger extends MessageLogger {
  public void fatal(String message) throws BytecodeException;
}

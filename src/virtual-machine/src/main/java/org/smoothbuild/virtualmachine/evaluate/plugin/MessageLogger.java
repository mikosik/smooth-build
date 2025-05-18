package org.smoothbuild.virtualmachine.evaluate.plugin;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public interface MessageLogger {
  public void fatal(String message) throws BytecodeException;

  public void error(String message) throws BytecodeException;

  public void warning(String message) throws BytecodeException;

  public void info(String message) throws BytecodeException;
}

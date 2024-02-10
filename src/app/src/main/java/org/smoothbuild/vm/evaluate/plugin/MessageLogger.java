package org.smoothbuild.vm.evaluate.plugin;

import org.smoothbuild.vm.bytecode.BytecodeException;

public interface MessageLogger {
  public void error(String message) throws BytecodeException;

  public void warning(String message) throws BytecodeException;

  public void info(String message) throws BytecodeException;
}

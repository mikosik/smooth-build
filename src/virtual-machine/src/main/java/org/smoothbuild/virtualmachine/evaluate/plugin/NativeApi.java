package org.smoothbuild.virtualmachine.evaluate.plugin;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public BytecodeF factory();

  public MessageLogger log();

  public ArrayB messages() throws BytecodeException;
}

package org.smoothbuild.virtualmachine.evaluate.plugin;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public BytecodeFactory factory();

  public MessageLogger log();

  public BArray messages() throws BytecodeException;
}

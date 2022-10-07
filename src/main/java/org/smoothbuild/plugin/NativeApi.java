package org.smoothbuild.plugin;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ArrayB;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public BytecodeF factory();

  public MessageLogger log();

  public ArrayB messages();
}

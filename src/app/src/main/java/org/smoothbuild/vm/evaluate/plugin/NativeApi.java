package org.smoothbuild.vm.evaluate.plugin;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public BytecodeF factory();

  public MessageLogger log();

  public ArrayB messages();
}

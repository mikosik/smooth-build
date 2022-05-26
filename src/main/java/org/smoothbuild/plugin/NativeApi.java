package org.smoothbuild.plugin;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.type.TypingB;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public BytecodeF factory();

  public TypingB typing();

  public MessageLogger log();

  public ArrayB messages();
}

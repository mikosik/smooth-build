package org.smoothbuild.plugin;

import org.smoothbuild.bytecode.ByteCodeFactory;
import org.smoothbuild.bytecode.obj.val.ArrayB;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public ByteCodeFactory factory();

  public MessageLogger log();

  public ArrayB messages();
}

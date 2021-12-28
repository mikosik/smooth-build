package org.smoothbuild.plugin;

import org.smoothbuild.db.bytecode.ObjFactory;
import org.smoothbuild.db.bytecode.obj.val.ArrayB;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public ObjFactory factory();

  public MessageLogger log();

  public ArrayB messages();
}

package org.smoothbuild.plugin;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.val.ArrayH;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native funcs.
 */
public interface NativeApi {
  public ObjFactory factory();

  public MessageLogger log();

  public ArrayH messages();
}

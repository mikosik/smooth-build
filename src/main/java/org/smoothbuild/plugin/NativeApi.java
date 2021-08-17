package org.smoothbuild.plugin;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.db.ObjectFactory;

/**
 * Implementation of NativeApi doesn't provide any thread safety and should be used
 * from one thread by native functions.
 */
public interface NativeApi {
  public ObjectFactory factory();

  public MessageLogger log();

  public Array messages();
}

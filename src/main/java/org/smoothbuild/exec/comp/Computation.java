package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public interface Computation {
  public Hash hash();

  public ConcreteType type();

  public Output execute(Input input, NativeApi nativeApi) throws ComputationException;
}

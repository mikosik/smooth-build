package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.spec.Spec;

public interface Algorithm {
  public Hash hash();

  public Spec type();

  public Output run(Input input, NativeApi nativeApi) throws Exception;
}

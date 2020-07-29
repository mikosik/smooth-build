package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.plugin.NativeApi;

public interface Algorithm {
  public Hash hash();

  public Spec type();

  public Output run(Input input, NativeApi nativeApi) throws Exception;
}

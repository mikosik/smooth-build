package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.record.type.BinaryType;

public interface Algorithm {
  public Hash hash();

  public BinaryType type();

  public Output run(Input input, NativeApi nativeApi) throws Exception;

  public TaskKind kind();
}

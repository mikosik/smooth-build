package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public interface Algorithm {
  public String name();

  public Hash hash();

  public ConcreteType type();

  public Output run(Input input, NativeApi nativeApi) throws Exception;

  public default String description() {
    return type().name() + " " + name();
  }

  public TaskKind kind();
}

package org.smoothbuild.task.compute;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public interface Algorithm {
  public HashCode hash();

  public Type resultType();

  public Output execute(Input input, ContainerImpl container);
}

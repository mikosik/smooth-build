package org.smoothbuild.task.base;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public interface Computation {
  public HashCode hash();

  public Type resultType();

  public Output execute(Input input, ContainerImpl container);
}

package org.smoothbuild.task.work;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.compute.Algorithm;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class TaskWorker {
  private final Algorithm algorithm;
  private final HashCode hash;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final CodeLocation codeLocation;

  public TaskWorker(Algorithm algorithm, HashCode hash, String name, boolean isInternal,
      boolean isCacheable, CodeLocation codeLocation) {
    this.algorithm = algorithm;
    this.hash = hash;
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.codeLocation = checkNotNull(codeLocation);
  }

  public Type resultType() {
    return algorithm.resultType();
  }

  public String name() {
    return name;
  }

  public boolean isInternal() {
    return isInternal;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public HashCode hash() {
    return hash;
  }

  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return algorithm.execute(input, container);
  }
}

package org.smoothbuild.task.work;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public abstract class TaskWorker {
  private final HashCode hash;
  private final Type type;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final CodeLocation codeLocation;

  public TaskWorker(HashCode hash, Type type, String name, boolean isInternal, boolean isCacheable,
      CodeLocation codeLocation) {
    this.hash = hash;
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.codeLocation = checkNotNull(codeLocation);
  }

  public Type resultType() {
    return type;
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

  public abstract TaskOutput execute(TaskInput input, NativeApiImpl nativeApi);
}

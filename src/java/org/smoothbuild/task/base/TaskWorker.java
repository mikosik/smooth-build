package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public abstract class TaskWorker<T extends SValue> {
  private final HashCode hash;
  private final SType<T> type;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final CodeLocation codeLocation;

  public TaskWorker(HashCode hash, SType<T> type, String name, boolean isInternal,
      boolean isCacheable, CodeLocation codeLocation) {
    this.hash = hash;
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.codeLocation = checkNotNull(codeLocation);
  }

  public SType<T> resultType() {
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

  public abstract TaskResult<T> execute(Iterable<? extends SValue> input, NativeApiImpl nativeApi);
}

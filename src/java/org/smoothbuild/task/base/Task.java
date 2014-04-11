package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.PluginApiImpl;

public abstract class Task<T extends SValue> {
  private final SType<T> type;
  private final String name;
  private final boolean isInternal;
  private final CodeLocation codeLocation;

  public Task(SType<T> type, String name, boolean isInternal, CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.codeLocation = checkNotNull(codeLocation);
  }

  public SType<T> type() {
    return type;
  }

  public String name() {
    return name;
  }

  public boolean isInternal() {
    return isInternal;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public abstract T execute(PluginApiImpl pluginApi);
}

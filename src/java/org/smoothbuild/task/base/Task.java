package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

public abstract class Task {
  private final String name;
  private final boolean isInternal;
  private final CodeLocation codeLocation;

  public Task(String name, boolean isInternal, CodeLocation codeLocation) {
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.codeLocation = checkNotNull(codeLocation);
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

  public abstract SValue execute(SandboxImpl sandbox);
}

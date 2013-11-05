package org.smoothbuild.task.base;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;

public abstract class Task {
  private final String name;
  private final boolean isInternal;
  private final CodeLocation codeLocation;

  public Task(String name, boolean isInternal, CodeLocation codeLocation) {
    this.name = name;
    this.isInternal = isInternal;
    this.codeLocation = codeLocation;
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

  public abstract Value execute(SandboxImpl sandbox);
}

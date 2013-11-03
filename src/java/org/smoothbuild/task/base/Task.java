package org.smoothbuild.task.base;

import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;

public interface Task {
  public String name();

  public boolean isInternal();

  public Value execute(SandboxImpl sandbox);
}

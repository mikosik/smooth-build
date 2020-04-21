package org.smoothbuild.exec.task.base;

import org.smoothbuild.lang.base.Space;

public interface Task {
  public TaskKind kind();

  public Space space();
}

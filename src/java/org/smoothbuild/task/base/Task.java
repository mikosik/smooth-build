package org.smoothbuild.task.base;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

public interface Task {
  public Value execute(Sandbox sandbox);
}

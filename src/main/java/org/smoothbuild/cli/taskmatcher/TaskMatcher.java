package org.smoothbuild.cli.taskmatcher;

import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.exec.compute.Task;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(Task task, List<Log> logs);
}

package org.smoothbuild.task.base;

import org.smoothbuild.task.exec.TaskGenerator;

public interface Taskable {
  public Task generateTask(TaskGenerator taskGenerator);
}

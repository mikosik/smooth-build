package org.smoothbuild.task.base;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.task.exec.TaskGenerator;

public interface Taskable<T extends SValue> {
  public Task<T> generateTask(TaskGenerator taskGenerator);
}

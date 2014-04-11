package org.smoothbuild.task.base;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.exec.TaskGenerator;

public interface Taskable<T extends SValue> {
  public Task<T> generateTask(TaskGenerator taskGenerator);
}

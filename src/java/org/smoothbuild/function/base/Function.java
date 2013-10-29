package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;

public interface Function {
  public Signature signature();

  public Type type();

  public Name name();

  public ImmutableMap<String, Param> params();

  public Task generateTask(TaskGenerator taskGenerator, Map<String, Result> arguments);
}

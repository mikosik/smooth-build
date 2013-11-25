package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;

public interface Function {
  public Signature signature();

  public SType<?> type();

  public Name name();

  public ImmutableMap<String, Param> params();

  public Task generateTask(TaskGenerator taskGenerator, Map<String, Result> arguments,
      CodeLocation codeLocation);
}

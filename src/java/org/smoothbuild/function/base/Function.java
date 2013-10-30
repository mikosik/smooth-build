package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;

public interface Function {
  public Signature signature();

  public Type type();

  public Name name();

  public ImmutableMap<String, Param> params();

  public LocatedTask generateTask(TaskGenerator taskGenerator, Map<String, Result> arguments,
      CodeLocation codeLocation);
}

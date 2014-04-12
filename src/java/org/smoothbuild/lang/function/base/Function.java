package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;

public interface Function<T extends SValue> {
  public Signature<T> signature();

  public SType<T> type();

  public Name name();

  public ImmutableMap<String, Param> params();

  public Task<T> generateTask(TaskGenerator taskGenerator,
      Map<String, ? extends Result<?>> arguments, CodeLocation codeLocation);
}

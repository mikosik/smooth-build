package org.smoothbuild.function.base;

import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;

public interface Function {
  public Signature signature();

  public Type type();

  public Name name();

  public ImmutableMap<String, Param> params();

  public Task generateTask(ImmutableMap<String, Task> dependencies);
}

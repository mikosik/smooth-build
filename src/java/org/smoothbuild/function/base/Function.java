package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public interface Function {
  public Signature signature();

  public Type type();

  public Name name();

  public HashCode hash();

  public ImmutableMap<String, Param> params();

  public Task generateTask(Map<String, Task> dependencies, CodeLocation codeLocation);
}

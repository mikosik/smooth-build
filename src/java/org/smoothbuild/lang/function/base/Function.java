package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface Function<T extends SValue> {
  public Signature<T> signature();

  public SType<T> type();

  public Name name();

  public ImmutableMap<String, Param> params();

  public ImmutableList<? extends Node<?>> dependencies(ImmutableMap<String, ? extends Node<?>> args);

  public TaskWorker<T> createWorker(ImmutableMap<String, ? extends Node<?>> args,
      CodeLocation codeLocation);
}

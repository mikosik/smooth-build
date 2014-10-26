package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface Function<T extends Value> {
  public Type<T> type();

  public Name name();

  public ImmutableList<Param> params();

  public ImmutableList<? extends Expr<?>> dependencies(
      ImmutableMap<String, ? extends Expr<?>> args);

  public TaskWorker<T> createWorker(ImmutableMap<String, ? extends Expr<?>> args,
      boolean isInternal, CodeLocation codeLocation);
}

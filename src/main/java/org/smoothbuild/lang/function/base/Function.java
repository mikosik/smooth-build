package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface Function<T extends Value> {
  public Type<T> type();

  public Name name();

  public ImmutableList<Parameter> parameters();

  public ImmutableList<? extends Expression<?>> dependencies(
      ImmutableMap<String, ? extends Expression<?>> args);
}

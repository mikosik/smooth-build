package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;

import com.google.common.collect.ImmutableList;

public interface Function<T extends Value> {
  public Type<T> type();

  public Name name();

  public ImmutableList<Parameter> parameters();
}

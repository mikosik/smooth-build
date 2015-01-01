package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

public interface Function {
  public Type type();

  public Name name();

  public ImmutableList<Parameter> parameters();
}

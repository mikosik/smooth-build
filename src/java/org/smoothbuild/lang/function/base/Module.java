package org.smoothbuild.lang.function.base;

import com.google.common.collect.ImmutableSet;

public interface Module {
  public boolean containsFunction(Name name);

  public Function getFunction(Name name);

  public ImmutableSet<Name> availableNames();
}

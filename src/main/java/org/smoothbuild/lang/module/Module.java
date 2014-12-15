package org.smoothbuild.lang.module;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableSet;

public interface Module {
  public boolean containsFunction(Name name);

  public Function getFunction(Name name);

  public ImmutableSet<Name> availableNames();
}

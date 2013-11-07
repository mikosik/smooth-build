package org.smoothbuild.function.base;

import com.google.common.collect.ImmutableSet;

public interface Module {

  public Function getFunction(Name name);

  public ImmutableSet<Name> availableNames();
}

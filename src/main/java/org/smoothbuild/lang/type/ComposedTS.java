package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;

public sealed interface ComposedTS permits ArrayTS, MonoFuncTS {
  public ImmutableList<MonoTS> covars();
  public ImmutableList<MonoTS> contravars();
}

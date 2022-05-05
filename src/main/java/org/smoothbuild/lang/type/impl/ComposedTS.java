package org.smoothbuild.lang.type.impl;

import com.google.common.collect.ImmutableList;

public sealed interface ComposedTS permits ArrayTS, FuncTS {
  public ImmutableList<TypeS> covars();
  public ImmutableList<TypeS> contravars();
}

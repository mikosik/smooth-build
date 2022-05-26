package org.smoothbuild.bytecode.type.cnst;

import com.google.common.collect.ImmutableList;

public sealed interface ComposedTB permits ArrayTB, FuncTB, TupleTB {
  public ImmutableList<TypeB> covars();
  public ImmutableList<TypeB> contravars();
}

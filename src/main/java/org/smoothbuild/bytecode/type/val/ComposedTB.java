package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.TypeB;

import com.google.common.collect.ImmutableList;

public sealed interface ComposedTB permits ArrayTB, FuncTB, TupleTB {
  public ImmutableList<TypeB> covars();
  public ImmutableList<TypeB> contravars();
}

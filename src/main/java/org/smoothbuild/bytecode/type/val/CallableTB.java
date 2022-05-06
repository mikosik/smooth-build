package org.smoothbuild.bytecode.type.val;

import com.google.common.collect.ImmutableList;

public sealed interface CallableTB permits FuncTB, MethodTB {
  public TypeB res();

  public ImmutableList<TypeB> params();

  public TupleTB paramsTuple();
}

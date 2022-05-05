package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.type.api.FuncT;

import com.google.common.collect.ImmutableList;

public sealed interface CallableTB extends FuncT permits FuncTB, MethodTB {
  @Override
  public TypeB res();

  @Override
  public ImmutableList<TypeB> params();

  public TupleTB paramsTuple();
}

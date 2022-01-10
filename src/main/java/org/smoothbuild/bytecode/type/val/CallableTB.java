package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.FuncT;

import com.google.common.collect.ImmutableList;

public interface CallableTB extends FuncT {
  @Override
  public TypeB res();

  @Override
  public ImmutableList<TypeB> params();

  public TupleTB paramsTuple();
}
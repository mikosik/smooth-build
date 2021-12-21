package org.smoothbuild.db.object.type.val;

import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.FuncT;

import com.google.common.collect.ImmutableList;

public interface CallableTB extends FuncT {
  @Override
  public TypeB res();

  @Override
  public ImmutableList<TypeB> params();

  public TupleTB paramsTuple();
}

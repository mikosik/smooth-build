package org.smoothbuild.db.object.type.val;

import org.smoothbuild.db.object.type.base.TypeB;

import com.google.common.collect.ImmutableList;

public interface CallableTB {
  public TypeB res();

  public ImmutableList<TypeB> params();

  public TupleTB paramsTuple();
}

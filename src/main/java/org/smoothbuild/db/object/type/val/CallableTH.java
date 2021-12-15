package org.smoothbuild.db.object.type.val;

import org.smoothbuild.db.object.type.base.TypeH;

import com.google.common.collect.ImmutableList;

public interface CallableTH {
  public TypeH res();

  public ImmutableList<TypeH> params();

  public TupleTH paramsTuple();
}

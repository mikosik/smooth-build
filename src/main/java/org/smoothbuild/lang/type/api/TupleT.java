package org.smoothbuild.lang.type.api;

import com.google.common.collect.ImmutableList;

public interface TupleT {
  public ImmutableList<? extends Type> items();
}
